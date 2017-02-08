package com.upbchain.pointcoin.wallet.api.service;

import java.util.Optional;

import com.upbchain.pointcoin.wallet.api.domain.MortgageAccount;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public class MortgageAccountException extends Exception {

    private static final long serialVersionUID = 2438700493374003501L;
    private Optional<MortgageAccount> account = null;
    
    protected MortgageAccountException(String message) {
        super(message);
        account = Optional.empty();
    }
    
    protected MortgageAccountException(MortgageAccount account) {
        super(account.getMemberId());
        this.account = Optional.of(account);
    }
    
    public Optional<MortgageAccount> getMortgageAccount() {
        return this.account;
    }
}