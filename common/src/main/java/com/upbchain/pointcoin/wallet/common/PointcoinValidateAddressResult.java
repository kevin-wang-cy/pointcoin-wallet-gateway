package com.upbchain.pointcoin.wallet.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public class PointcoinValidateAddressResult {
    private boolean valid = false;
    private boolean mine = false;
    private String account = "";
    private String ticker = "";

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public boolean isValid() {
        return valid;
    }

    @JsonProperty("isvalid")
    public void setValid(boolean isvalid) {
        this.valid = isvalid;
    }

    public boolean isMine() {
        return mine;
    }

    @JsonProperty("ismine")
    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
