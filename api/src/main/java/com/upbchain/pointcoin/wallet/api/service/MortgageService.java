package com.upbchain.pointcoin.wallet.api.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upbchain.pointcoin.wallet.api.domain.MortgageAccount;
import com.upbchain.pointcoin.wallet.api.domain.MortgageDepositRecord;
import com.upbchain.pointcoin.wallet.api.domain.MortgageDepositRecordId;
import com.upbchain.pointcoin.wallet.api.repository.MortgageAccountRepository;
import com.upbchain.pointcoin.wallet.api.repository.MortgageDepositRecordResposity;
import com.upbchain.pointcoin.wallet.common.InvalidPointcoinWalletMortgageMemberException;
import com.upbchain.pointcoin.wallet.common.PointcoinAccountTransaction;
import com.upbchain.pointcoin.wallet.common.PointcoinTransaction;
import com.upbchain.pointcoin.wallet.common.PointcoinTransactionOutput;
import com.upbchain.pointcoin.wallet.common.PointcoinValidateAddressResult;
import com.upbchain.pointcoin.wallet.common.PointcoinWalletClient;
import com.upbchain.pointcoin.wallet.common.PointcoinWalletRPCException;
import com.upbchain.pointcoin.wallet.common.util.PointcoinWalletUtil;

@Service
@Transactional
public class MortgageService {
    private static final Logger LOG = LoggerFactory.getLogger(MortgageService.class);
    
    @Autowired
    private MortgageAccountRepository mortgageAccountRepository;
    
    @Autowired
    private MortgageDepositRecordResposity mortgageDepositRecordResposity;
    
    @Autowired
    private PointcoinWalletClient pointcoinWalletClient;
    
        
    public @NotNull MortgageAccount createMortgageAccount(@NotNull String memberId) throws MortgageAccountAlreadyExistException, InvalidPointcoinWalletMortgageMemberException, PointcoinWalletRPCException {
        
        MortgageAccount account = mortgageAccountRepository.findOne(memberId);
        
        if (account != null) {
            throw new MortgageAccountAlreadyExistException(account);
        }
                
        account = MortgageAccount.newInstance(memberId);
        account.setPointcoinAddress(pointcoinWalletClient.generateMemberMortgageAccountAddress(memberId));
        account.setAlias(pointcoinWalletClient.getAlias());
        account.setRecieved(pointcoinWalletClient.getRecievedByAddress(account.getPointcoinAddress()));
        
        
        account = mortgageAccountRepository.saveAndFlush(account);
        
        return account;
    }
    
    public MortgageAccount retrieveMortgageAccount(@NotNull String memberId) throws MortgageAccountViolationException, MortgageAccountNotExistException, PointcoinWalletRPCException {
        return this.internalRetrieveMortgageAccount(memberId);
    }
        
    public @NotNull List<MortgageDepositRecord> syncLatestPointcoinTransactionIntoAccount(@NotNull String memberId, int latestN) throws MortgageAccountViolationException, MortgageAccountNotExistException, InvalidPointcoinWalletMortgageMemberException, PointcoinWalletRPCException {
        if (latestN <= 0) latestN = 24;
        
        List<PointcoinAccountTransaction> txList = pointcoinWalletClient.retrieveLatestMortgageDepositTransactions(memberId, latestN, 1);

        Function<PointcoinAccountTransaction, MortgageDepositRecord> toDepositRecordMapper = new Function<PointcoinAccountTransaction, MortgageDepositRecord>() {
            public MortgageDepositRecord apply(PointcoinAccountTransaction it) {
                                
                MortgageDepositRecord ret = MortgageDepositRecord.newInstance(MortgageDepositRecordId.newInstance(memberId, it.getTxid(), it.getAddress()));
                
                ret.setAmount(it.getAmount());
                ret.setConfirmations(it.getConfirmations());
                ret.setCurrency(it.getCurrency());
                ret.setTxReceivedTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(it.getTimereceived()), ZoneId.systemDefault()));
                ret.setTxTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(it.getTime()), ZoneId.systemDefault()));
                
                return ret;
            }
        };
        
        List<MortgageDepositRecord> records =  txList.stream().map(toDepositRecordMapper).filter(it -> mortgageDepositRecordResposity.findOne(it.getRecordId()) == null).collect(toList());
        
        records = mortgageDepositRecordResposity.save(records);
        
        mortgageDepositRecordResposity.flush();
        
        return records;
    }
    
    public @NotNull BootTransactionIntoAccountResult bookPointcoinTransactionIntoAccount(@NotNull String txId, Optional<String> memberIdOpt) throws MortgageTransactionNotExistException, MortgageTransactionInvalidException, PointcoinWalletRPCException {
        final PointcoinTransaction pointcoinTx = pointcoinWalletClient.retrievePoincoinTransactionByTxId(txId);
        
        if (pointcoinTx == null) {
            PointcoinTransaction emptTx = new PointcoinTransaction();
            emptTx.setTxId(txId);
            throw new MortgageTransactionNotExistException(emptTx);
        }
        
        BootTransactionIntoAccountResult bookResult = new BootTransactionIntoAccountResult();
        
        Predicate<PointcoinTransaction> confirmationPredicate = it -> it.getConfirmations() >= 1;
        Predicate<PointcoinTransaction> notCoinbasedPredicate = it -> !it.isCoinbased();
        
        Function<PointcoinTransactionOutput, Map<String, Object>> txOutputMapper = new Function<PointcoinTransactionOutput, Map<String, Object>>() {
            public Map<String, Object> apply(PointcoinTransactionOutput txOutput) {
                
                Map<String, Object> ret = new HashMap<>();
                
                ret.put("txOutput", txOutput);
                
                try {
                    PointcoinValidateAddressResult validateResult = pointcoinWalletClient.verifyPointcoinAddress(txOutput.getAddresses().get(0));
                    
                    ret.put("validateResult", validateResult);
                }catch (PointcoinWalletRPCException ex){
                    ret.put("validateResult", new PointcoinValidateAddressResult());
                    if (LOG.isWarnEnabled()) {
                        LOG.warn(String.format("!!!Skipped handling PointcoinTransactionOutput [%s] while trying to booking transaction [%s] due to RPC JOSN ERROR!!", txOutput, txId));
                    }
                }
                
                return ret;
            }
        };
        
        Function<Map<String, Object>, MortgageDepositRecord> toDepositRecordMapper = new Function<Map<String, Object>, MortgageDepositRecord>() {
            public MortgageDepositRecord apply(Map<String, Object> it) {
                PointcoinValidateAddressResult validateResult = (PointcoinValidateAddressResult) it.get("validateResult");
                PointcoinTransactionOutput txOutput = (PointcoinTransactionOutput) it.get("txOutput");
                
                String memberId = PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(validateResult.getAccount()).get();
                String pointcoinAddress = txOutput.getAddresses().get(0);
                
                MortgageDepositRecord ret = MortgageDepositRecord.newInstance(MortgageDepositRecordId.newInstance(memberId, pointcoinTx.getTxId(), pointcoinAddress));
                
                ret.setAmount(txOutput.getValue());
                ret.setConfirmations(pointcoinTx.getConfirmations());
                ret.setCurrency(txOutput.getCurrency());
                ret.setTxReceivedTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(pointcoinTx.getTimereceived()), ZoneId.systemDefault()));
                ret.setTxTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(pointcoinTx.getTime()), ZoneId.systemDefault()));
                
                return ret;
            }
        };
        
        Predicate<Map<String, Object>> validTxOuputPredicate = it -> {
            PointcoinValidateAddressResult validateResult = (PointcoinValidateAddressResult) it.get("validateResult");
            Optional<String> txMemberIdOpt = PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(validateResult.getAccount());
            
            return validateResult.isValid() && validateResult.isMine() && txMemberIdOpt.isPresent() && (memberIdOpt.isPresent() ? memberIdOpt.get().equals(txMemberIdOpt.get()) : true);
        };
        
        Predicate<PointcoinTransactionOutput> haveOutputPredicate = it -> BigDecimal.ZERO.compareTo(it.getValue()) < 0 && it.getAddresses().size() == 1;
        
        List<MortgageDepositRecord> records = new ArrayList<MortgageDepositRecord>();
        if (confirmationPredicate.and(notCoinbasedPredicate).test(pointcoinTx)) {
            
            Map<MortgageDepositRecordId, List<MortgageDepositRecord>> mapsRecords = pointcoinTx.getTxOutputs().stream().filter(haveOutputPredicate).map(txOutputMapper).filter(validTxOuputPredicate).map(toDepositRecordMapper)
                    .collect(groupingBy(MortgageDepositRecord::getRecordId, toList()));
            
            if (mapsRecords.size() == 0) {
                throw new MortgageTransactionInvalidException(pointcoinTx);
            }
            
            for (MortgageDepositRecordId it : mapsRecords.keySet()) {
                MortgageDepositRecord existRecord = mortgageDepositRecordResposity.findOne(it);
                
                MortgageDepositRecord accumatedRecord = MortgageDepositRecord.newInstance(it);
                for (MortgageDepositRecord itt : mapsRecords.get(it)) {
                    accumatedRecord.setAmount(accumatedRecord.getAmount().add(itt.getAmount()));

                    accumatedRecord.setConfirmations(itt.getConfirmations());
                    accumatedRecord.setCurrency(itt.getCurrency());
                    accumatedRecord.setTxReceivedTime(itt.getTxReceivedTime());
                    accumatedRecord.setTxTime(itt.getTxTime());
                };
                
                if (existRecord == null) {
                    records.add(accumatedRecord);
                }
                else {
                    bookResult.addOldRecord(existRecord);
                    if (!(accumatedRecord.getAmount().compareTo(existRecord.getAmount()) == 0)) {
                        bookResult.addInconsistentRecordId(it);
                        
                        if (LOG.isWarnEnabled()) {
                            LOG.warn(String.format("!!!!Skipped handling MortgageDepositRecord [%s] while trying to booking transaction [%s] due to it had been handled before, but the amount in current transaction is %s whereas %s in DB!", it, txId, accumatedRecord.getAmount(), existRecord.getAmount()));
                        }
                    }
                }                
            }
            
            if (records.size() > 0) {
                records = mortgageDepositRecordResposity.save(records);
                
                mortgageDepositRecordResposity.flush();
                
                bookResult.addNewRecords(records);
            }
        }
        else {
            throw new MortgageTransactionInvalidException(pointcoinTx);
        }
        
        return bookResult;
    }
    
    public @NotNull List<MortgageDepositRecord> retrieveMemberMortgageDepositRecord(@NotNull String memberId) {
        return mortgageDepositRecordResposity.findByRecordIdMemberId(memberId);
    }
    
    public @NotNull List<MortgageDepositRecord> retrieveMemberMortgageDepositRecord(@NotNull String memberId, @NotNull String txId) {
        return mortgageDepositRecordResposity.findByRecordIdMemberIdAndRecordIdTxId(memberId, txId);
    }
    
    public @NotNull List<MortgageDepositRecord> retrieveMemberUnSyncedMortgageDepositRecord(@NotNull String memberId) {
        return mortgageDepositRecordResposity.findByRecordIdMemberIdAndSyncStatusSyncedWithMemberFalse(memberId);
    }
    
    public @NotNull List<MortgageDepositRecord> retrieveMortgageDepositRecord(@NotNull String txId) {
        return mortgageDepositRecordResposity.findByRecordIdTxId(txId);
    }
    
    public @NotNull List<MortgageDepositRecord> retrieveUnSyncedMortgageDepositRecord() {
        return mortgageDepositRecordResposity.findBySyncStatusSyncedWithMemberFalse();
    }
    
    private @NotNull MortgageAccount internalRetrieveMortgageAccount(@NotNull String memberId) throws MortgageAccountViolationException, MortgageAccountNotExistException, PointcoinWalletRPCException {
        
        MortgageAccount account = mortgageAccountRepository.findOne(memberId);
        
        if (account == null) {
            throw new MortgageAccountNotExistException(memberId);
        }
        
        Boolean integrity = pointcoinWalletClient.isMemberMortgageAccountAddress(memberId, account.getPointcoinAddress());
        
        if (!integrity) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(String.format("!!Detected non-integrity member mortgage account: [%s, %s]", account.getMemberId(), account.getPointcoinAddress()));
            }
            
            throw new MortgageAccountViolationException(account);
        }
        
        
        try {
            account.setRecieved(pointcoinWalletClient.getRecievedByAddress(account.getPointcoinAddress()));
            account = mortgageAccountRepository.saveAndFlush(account);
        }
        catch (PointcoinWalletRPCException ex) {
           if (LOG.isWarnEnabled()) {
               LOG.warn("Not able to updating account received value due to RPC error!!");
           }
        }
                
        return account;
    }
    
    public void markMortgageDepositRecordAsSynced(List<MortgageDepositRecordId> recordIds) {
        List<MortgageDepositRecord> records = mortgageDepositRecordResposity.findAll(recordIds);

        records.stream().filter(it -> !it.getSyncStatus().getSyncedWithMember()).forEach(it -> {
            it.getSyncStatus().setSyncedWithMember(true);
            it.getSyncStatus().setLastSyncAt(LocalDateTime.now());
            mortgageDepositRecordResposity.save(it);
        });

        mortgageDepositRecordResposity.flush();
    }
    
    public static class BootTransactionIntoAccountResult {
        private final List<MortgageDepositRecord> newRecords = new ArrayList<>();
        private final List<MortgageDepositRecord> oldRecords = new ArrayList<>();
        private final List<MortgageDepositRecordId> inconsistentRecordIds = new ArrayList<>();
        
        private BootTransactionIntoAccountResult() {
        }

        public List<MortgageDepositRecord> getNewRecords() {
            return newRecords;
        }

        private boolean addNewRecords(@NotNull List<MortgageDepositRecord> newRecords) {
            return this.newRecords.addAll(newRecords);
        }

        public List<MortgageDepositRecord> getOldRecords() {
            return oldRecords;
        }

        private boolean addOldRecords(List<MortgageDepositRecord> oldRecords) {
            return this.oldRecords.addAll(oldRecords);
        }
        
        private boolean addOldRecord(MortgageDepositRecord oldRecord) {
            return this.oldRecords.add(oldRecord);
        }
        
        public List<MortgageDepositRecordId> getInconsistentRecordIds() {
            return inconsistentRecordIds;
        }

        private boolean addInconsistentRecordIds(List<MortgageDepositRecordId> inconsistentRecordIds) {
            return this.inconsistentRecordIds.addAll(inconsistentRecordIds);
        }
        
        private boolean addInconsistentRecordId(MortgageDepositRecordId inconsistentRecordId) {
            return this.inconsistentRecordIds.add(inconsistentRecordId);
        }
    }
}
