package com.upbchain.pointcoin.wallet.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HelloService {
    @Value("${message:World}")
    private String msg;

    public String message() {
        return this.msg;
    }
}