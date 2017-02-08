package com.upbchain.pointcoin.wallet.api.resource.util;

public class ErrorMessage {
    
    private int status;
    private String message;
    private Object entity;
    
    public static ErrorMessage newInstance() {
        return new ErrorMessage();
    }
    public ErrorMessage status(int status) {
        this.setStatus(status);
        
        return this;
    }
    
    public ErrorMessage message(String message) {
        this.setMessage(message);
        
        return this;
    }
    
    public ErrorMessage entity(Object entity) {
        this.setEntity(entity);
        
        return this;
    }
    
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Object getEntity() {
        return entity;
    }
    public void setEntity(Object entity) {
        this.entity = entity;
    }
}
