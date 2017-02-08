package com.upbchain.pointcoin.wallet.common;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public class PointcoinWalletRPCException extends Exception {
    private static final long serialVersionUID = -2959749460795187406L;
    
    private final String command;
    private final List<?> params;
    
    public PointcoinWalletRPCException(@NotNull String command, @NotNull List<?> params, Throwable cause) {
        super(cause);
        
        this.command = command;
        this.params = params;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public List<?> getParams() {
        return this.params;
    }
}