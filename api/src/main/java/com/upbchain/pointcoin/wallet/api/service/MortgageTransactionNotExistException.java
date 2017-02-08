package com.upbchain.pointcoin.wallet.api.service;

import com.upbchain.pointcoin.wallet.common.PointcoinTransaction;

public class MortgageTransactionNotExistException extends MortgageTransactionException {

    private static final long serialVersionUID = 2438700493374003501L;
       
    protected MortgageTransactionNotExistException(PointcoinTransaction tx) {
        super(tx, String.format("Transaction of '%s' doesn't exists.", tx.getTxId()));
    }
}