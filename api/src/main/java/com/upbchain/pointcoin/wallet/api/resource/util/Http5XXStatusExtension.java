package com.upbchain.pointcoin.wallet.api.resource.util;

import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public enum Http5XXStatusExtension implements StatusType {
    DATA_VIOLATION(550, "Data Violation Detected.");
    
    private final int code;
    private final String reason;
    private final Family family;
    
    Http5XXStatusExtension(final int statusCode, final String reasonPhrase) {
        this.code = statusCode;
        this.reason = reasonPhrase;
        this.family = Family.familyOf(statusCode);
    }
    
    /**
     * Get the class of status code.
     *
     * @return the class of status code.
     */
    @Override
    public Family getFamily() {
        return family;
    }

    /**
     * Get the associated status code.
     *
     * @return the status code.
     */
    @Override
    public int getStatusCode() {
        return code;
    }

    /**
     * Get the reason phrase.
     *
     * @return the reason phrase.
     */
    @Override
    public String getReasonPhrase() {
        return toString();
    }

    /**
     * Get the reason phrase.
     *
     * @return the reason phrase.
     */
    @Override
    public String toString() {
        return reason;
    }
}
