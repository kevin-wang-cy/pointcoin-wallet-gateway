package com.upbchain.pointcoin.wallet.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PointcoinTransaction {
    private long confirmations;
    private long time;
    private long timereceived;

    private String txId;

    private List<PointcoinTransactionInput> txInputs;

    private List<PointcoinTransactionOutput> txOutputs;
    
    private boolean generated = false;

    public long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations = confirmations;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTimereceived() {
        return timereceived;
    }

    public void setTimereceived(long timereceived) {
        this.timereceived = timereceived;
    }

    public String getTxId() {
        return txId;
    }

    @JsonProperty("txid")
    public void setTxId(String txId) {
        this.txId = txId;
    }

    public List<PointcoinTransactionInput> getTxInputs() {
        return txInputs;
    }

    @JsonProperty("vin")
    public void setTxInputs(List<PointcoinTransactionInput> txInputs) {
        this.txInputs = txInputs;
    }

    public List<PointcoinTransactionOutput> getTxOutputs() {
        return txOutputs;
    }

    @JsonProperty("vout")
    public void setTxOutputs(List<PointcoinTransactionOutput> txOutputs) {
        this.txOutputs = txOutputs;
    }

    public boolean isCoinbased() {
        return this.getTxInputs().size() == 0 || isGenerated();
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }
}
