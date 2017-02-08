package com.upbchain.pointcoin.wallet.api.service;

import com.upbchain.pointcoin.wallet.common.PointcoinTransaction;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public class MortgageTransactionNotConfirmedException extends MortgageTransactionInvalidException {

    private static final long serialVersionUID = 2438700493374003501L;
       
    protected MortgageTransactionNotConfirmedException(PointcoinTransaction tx) {
        super(tx);
    }
}