package com.upbchain.pointcoin.wallet.api.resource;

import com.upbchain.pointcoin.wallet.api.domain.MortgageAccount;
import com.upbchain.pointcoin.wallet.api.domain.MortgageDepositRecord;
import com.upbchain.pointcoin.wallet.api.resource.util.ErrorMessage;
import com.upbchain.pointcoin.wallet.api.resource.util.Http5XXStatusExtension;
import com.upbchain.pointcoin.wallet.api.service.*;
import com.upbchain.pointcoin.wallet.api.service.MortgageService.BootTransactionIntoAccountResult;
import com.upbchain.pointcoin.wallet.common.*;
import com.upbchain.pointcoin.wallet.common.util.PointcoinWalletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;

@Path("/wallet")
public class WalletResource {
    private static final Logger LOG = LoggerFactory.getLogger(WalletResource.class);

    @Autowired
    private PointcoinWalletClient pointcoinWalletClient;

    @GET
    @Path("/transactions/{txId}")
    @Produces(MediaType.APPLICATION_JSON) 
    public Response retrieveTransactionById(@NotNull @PathParam("txId") String txId ){

        try {
            PointcoinTransaction tx = pointcoinWalletClient.retrievePoincoinTransactionByTxId(txId);

            if (tx == null) {
                return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.newInstance().status(Response.Status.NOT_FOUND.getStatusCode()).message("Transaction doesn't exist.").entity(txId)).encoding("UTF-8").build();
            }

            boolean isMine = !(tx.isCoinbased() || tx.isGenerated());

            if (isMine) {
                for (PointcoinTransactionInput vin : tx.getTxInputs()) {
                    for (String addr : vin.getAddresses()) {
                        PointcoinValidateAddressResult result = pointcoinWalletClient.verifyPointcoinAddress(addr);
                        isMine = isMine && result.isMine() && result.isValid();

                        if (!isMine) break;
                    }

                    if (!isMine) break;
                }
            }

            return Response.ok(tx).header("X-MINE", isMine).encoding("UTF-8").build();
        }
        catch (PointcoinWalletRPCException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ErrorMessage.newInstance().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).message(ex.getCause().getMessage())).encoding("UTF-8").build();
        }
    }


    @GET
    @Path("/addresses/{addr}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateAddress(@NotNull @PathParam("addr") String address ){

        try {
            PointcoinValidateAddressResult result = pointcoinWalletClient.verifyPointcoinAddress(address);

            if (result == null || !result.isValid()) {
                return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.newInstance().status(Response.Status.NOT_FOUND.getStatusCode()).message("Address doesn't exist.").entity(address)).encoding("UTF-8").build();
            }

            Optional<String> memId = PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(result.getAccount());

            if (memId.isPresent()) {
                result.setAccount("**XGX Member Deposit Account**");
            }

            return Response.ok(result).header("X-MINE", result.isMine()).encoding("UTF-8").build();
        }
        catch (PointcoinWalletRPCException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ErrorMessage.newInstance().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).message(ex.getCause().getMessage())).encoding("UTF-8").build();
        }
    }

}
