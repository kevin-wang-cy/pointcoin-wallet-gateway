package com.upbchain.pointcoin.wallet.common;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public class InvalidPointcoinWalletMortgageMemberException extends Exception {
    private static final long serialVersionUID = -2959749460795187406L;
    private final String memberId;
    
    public InvalidPointcoinWalletMortgageMemberException(String memberId) {
        super(memberId + " is not an valid member id to open a pointcoin wallet mortgage deposit account.");
        this.memberId = memberId;
    }

    public String getMemberId() {
        return this.memberId;
    }
}