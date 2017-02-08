package com.upbchain.pointcoin.wallet.api.service;

import com.upbchain.pointcoin.wallet.common.PointcoinTransaction;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public class MortgageTransactionInvalidException extends MortgageTransactionException {

    private static final long serialVersionUID = 2438700493374003501L;
       
    protected MortgageTransactionInvalidException(PointcoinTransaction tx) {
        super(tx, String.format("Transaction of '%s' is not a valid mortgage deposity transaction.", tx.getTxId()));
    }
}