package com.upbchain.pointcoin.wallet.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PointcoinTransactionOutput {
    private int index;
    private String currency;
    private BigDecimal value;
    private List<String> addresses = new ArrayList<>();

    @JsonProperty("scriptPubKey")
    public void setAddresses(Map<String, Object> scriptPubKeyObj) {
        @SuppressWarnings("unchecked")
        ArrayList<String> addresses = (ArrayList<String>) scriptPubKeyObj.get("addresses");
        
        this.addresses = addresses == null ? new ArrayList<>() : addresses;
    }

    public int getIndex() {
        return index;
    }

    @JsonProperty("n")
    public void setIndex(int index) {
        this.index = index;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(@NotNull List<String> addresses) {
        this.addresses = addresses;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof PointcoinTransactionOutput)) {
            return false;
        }
        PointcoinTransactionOutput it = (PointcoinTransactionOutput) o;

        if (this.getValue() == null && it.getValue() !=null || this.getValue() != null && it.getValue() ==null) {
            return false;
        }
        
        return this.getIndex() == it.getIndex() && Objects.equals(this.getAddresses(), it.getAddresses())
                && Objects.equals(this.getCurrency(), it.getCurrency()) && (Objects.equals(this.getValue(), it.getValue()) || this.getValue().compareTo(it.getValue()) == 0 );
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getIndex(), this.getAddresses(), this.getCurrency(), this.getValue());
    }
}