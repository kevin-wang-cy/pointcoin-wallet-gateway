package com.upbchain.pointcoin.wallet.api.domain;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

@Entity
public class MortgageDepositRecord extends AbstractAuditableEntity {

    @EmbeddedId
    private MortgageDepositRecordId recordId;

    @Column(updatable = false, precision = 18, scale = 8)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(updatable = false, length = 4)
    private String currency;

    @Column(updatable = true)
    private Long confirmations;

    @Column(updatable = false)
    ZonedDateTime txTime;

    @Column(updatable = false)
    ZonedDateTime txReceivedTime;

    @Embedded
    MortgageDepositRecordSyncStatus syncStatus;

    @PrePersist
    public void prePersistEvent()
    {
        if ( getSyncStatus() == null )
        {
            setSyncStatus( MortgageDepositRecordSyncStatus.newInstance(null, false));
        }
    }
    
    private MortgageDepositRecord() {

    }

    public static MortgageDepositRecord newInstance(@NotNull MortgageDepositRecordId id) {
        MortgageDepositRecord ret = new MortgageDepositRecord();

        ret.setRecordId(id);

        return ret;
    }

    public MortgageDepositRecordId getRecordId() {
        return recordId;
    }

    public void setRecordId(MortgageDepositRecordId recordId) {
        this.recordId = recordId;
    }

    public MortgageDepositRecordSyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(MortgageDepositRecordSyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Long confirmations) {
        this.confirmations = confirmations;
    }

    public ZonedDateTime getTxTime() {
        return txTime;
    }

    public void setTxTime(ZonedDateTime txTime) {
        this.txTime = txTime;
    }

    public ZonedDateTime getTxReceivedTime() {
        return txReceivedTime;
    }

    public void setTxReceivedTime(ZonedDateTime txReceivedTime) {
        this.txReceivedTime = txReceivedTime;
    }

}
