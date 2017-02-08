package com.upbchain.pointcoin.wallet.common;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClient.RequestListener;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.upbchain.pointcoin.wallet.common.util.PointcoinWalletUtil;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public class PointcoinWalletClient {
    private static final Logger LOG = LoggerFactory.getLogger(PointcoinWalletClient.class);
    
    private final String alias;
    private final JsonRpcHttpClient jsonRPCClient;

    public PointcoinWalletClient(@NotNull String alias, @NotNull URL serviceUrl) {
        this.alias = alias;
        this.jsonRPCClient = createJsonRPCClient(serviceUrl, new HashMap<String, String>());
    }

    public PointcoinWalletClient(@NotNull String alias, @NotNull URL serviceUrl, @NotNull String userName, @NotNull String passsword) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", String.format("Basic %s", new String(Base64.getEncoder().encode(String.format("%s:%s", userName, passsword).getBytes()))));
        
        this.alias = alias;
        this.jsonRPCClient = createJsonRPCClient(serviceUrl, headers);
    }

    private JsonRpcHttpClient createJsonRPCClient(@NotNull URL serviceUrl, Map<String, String> headers) {
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("Pointcoin Wallet JSONRPC EndPoint: %s", serviceUrl.toExternalForm()));
        }
        ObjectMapper objMapper = new ObjectMapper();

        objMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        JsonRpcHttpClient rpcClient = new JsonRpcHttpClient(objMapper, serviceUrl, headers);

        rpcClient.setRequestListener(new RequestListener() {

            @Override
            public void onBeforeResponseProcessed(JsonRpcClient client, ObjectNode response) {
                if (LOG.isDebugEnabled()) LOG.debug(response.toString());
            }

            @Override
            public void onBeforeRequestSent(JsonRpcClient client, ObjectNode request) {
                if (LOG.isDebugEnabled()) LOG.debug(request.toString());
            }
        });

        return rpcClient;
    }
    
    public String getAlias() {
        return this.alias;
    }
    
    public String getMemberMortgageAccountAddress(@NotNull String memberId) throws PointcoinWalletRPCException, InvalidPointcoinWalletMortgageMemberException {
        Optional<String> accountName = PointcoinWalletUtil.buildMortgageDepositAccountName(memberId);

        if (!accountName.isPresent()) {
            throw new InvalidPointcoinWalletMortgageMemberException(memberId);
        }
        
        List<?> params = Arrays.asList(accountName.get());
        try {
            return jsonRPCClient.invoke("getaccountaddress", params, String.class);
        } catch (Throwable ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(String.format("JSON PRC Error while calling '%s' with parameters of '%s'", "getaccountaddress", params), ex);
            }
            throw new PointcoinWalletRPCException("getaccountaddress", params, ex);
        }
    }

    public BigDecimal getRecievedByAddress(@NotNull String pointcoinAddress) throws PointcoinWalletRPCException {
        List<?> params =  Arrays.asList(pointcoinAddress);
        
        try {
            return jsonRPCClient.invoke("getreceivedbyaddress", params, BigDecimal.class);
        } catch (Throwable ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(String.format("JSON PRC Error while calling '%s' with parameters of '%s'", "getreceivedbyaddress", params), ex);
            }
            throw new PointcoinWalletRPCException("getreceivedbyaddress", params, ex);
        }
    }

    public PointcoinValidateAddressResult verifyPointcoinAddress(@NotNull String pointcoinAddress) throws PointcoinWalletRPCException {
        List<?> params =  Arrays.asList(pointcoinAddress);
        
        try {
            return jsonRPCClient.invoke("validateaddress", params, PointcoinValidateAddressResult.class);
        } catch (Throwable ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(String.format("JSON PRC Error while calling '%s' with parameters of '%s'", "validateaddress", params), ex);
            }
            throw new PointcoinWalletRPCException("validateaddress", params, ex);
        }
    }

    public Boolean isMemberMortgageAccountAddress(@NotNull String memberId, @NotNull String pointcoinAddress) throws PointcoinWalletRPCException {
        PointcoinValidateAddressResult validateResult = verifyPointcoinAddress(pointcoinAddress);

        Optional<String> accountName = PointcoinWalletUtil.buildMortgageDepositAccountName(memberId);

        return (validateResult.isValid() && validateResult.isMine() && accountName.isPresent() && accountName.get().equals(validateResult.getAccount()));
    }

    public @NotNull List<PointcoinAccountTransaction> retrieveLatestMortgageDepositTransactions(@NotNull String memberId, int count, int minimalConfirm)
            throws PointcoinWalletRPCException, InvalidPointcoinWalletMortgageMemberException {
        Optional<String> accountName = PointcoinWalletUtil.buildMortgageDepositAccountName(memberId);

        if (!accountName.isPresent()) {
            throw new InvalidPointcoinWalletMortgageMemberException(memberId);
        }

        Predicate<PointcoinAccountTransaction> receivedPredicate = it -> "receive".equalsIgnoreCase(it.getCategory());
        Predicate<PointcoinAccountTransaction> positiveAmontPredicate = it -> BigDecimal.ZERO.compareTo(it.getAmount()) < 0;
        Predicate<PointcoinAccountTransaction> atLeastNConfirmPredicate = it -> it.getConfirmations() >= minimalConfirm;

        PointcoinAccountTransaction[] records = null;
        List<?> params = Arrays.asList(accountName.get(), count);
        try {
            records = jsonRPCClient.invoke("listtransactions", params, PointcoinAccountTransaction[].class);
        } catch (Throwable ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(String.format("JSON PRC Error while calling '%s' with parameters of '%s'", "listtransactions", params), ex);
            }
            throw new PointcoinWalletRPCException("listtransactions", params, ex);
        }

        return Arrays.asList(records).stream().filter(receivedPredicate.and(positiveAmontPredicate).and(atLeastNConfirmPredicate)).collect(Collectors.toList());
    }

    public PointcoinTransaction retrievePoincoinTransactionByTxId(@NotNull String txId) throws PointcoinWalletRPCException {
        List<?> params = Arrays.asList(txId);
        try {
            return jsonRPCClient.invoke("gettransaction", params, PointcoinTransaction.class);
        } catch (Throwable ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(String.format("JSON PRC Error while calling '%s' with parameters of '%s'", "gettransaction", params), ex);
            }
            throw new PointcoinWalletRPCException("gettransaction", params, ex);
        }
    }

}
