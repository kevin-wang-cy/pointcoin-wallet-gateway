package com.upbchain.pointcoin.wallet.api.service;

import com.upbchain.pointcoin.wallet.api.domain.MortgageAccount;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public class MortgageAccountNotExistException extends MortgageAccountException {

    private static final long serialVersionUID = 2438700493374003501L;
    
    protected MortgageAccountNotExistException(String memberId) {
        super(MortgageAccount.newInstance(memberId));
    }
}
