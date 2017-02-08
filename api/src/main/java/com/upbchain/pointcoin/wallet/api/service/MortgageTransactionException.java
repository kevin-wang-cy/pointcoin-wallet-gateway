package com.upbchain.pointcoin.wallet.api.service;

import com.upbchain.pointcoin.wallet.common.PointcoinTransaction;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public class MortgageTransactionException extends Exception {

    private static final long serialVersionUID = 2438700493374003501L;
    private PointcoinTransaction tx = null;
       
    protected MortgageTransactionException(PointcoinTransaction tx, String message) {
        super(message);
        this.tx = tx;
    }
    
    public PointcoinTransaction getPointcoinTransaction() {
        return this.tx;
    }
}