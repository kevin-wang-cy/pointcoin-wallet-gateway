package com.upbchain.pointcoin.wallet.common;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PointcoinTransactionInput {
    @JsonProperty("txid")
    private String txId;
    private int vout;
    private List<String> addresses;
    private BigDecimal value;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public int getVout() {
        return vout;
    }

    public void setVout(int vout) {
        this.vout = vout;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof PointcoinTransactionInput)) {
            return false;
        }
        PointcoinTransactionInput it = (PointcoinTransactionInput) o;

        if (this.getValue() == null && it.getValue() !=null || this.getValue() != null && it.getValue() ==null) {
            return false;
        }
        
        return Objects.equals(this.getTxId(), it.getTxId()) && Objects.equals(this.getAddresses(), it.getAddresses()) && this.getVout() == it.getVout()
                && (Objects.equals(this.getValue(), it.getValue()) || this.getValue().compareTo(it.getValue()) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTxId(), this.getAddresses(), this.getVout(), this.getValue());
    }
}
