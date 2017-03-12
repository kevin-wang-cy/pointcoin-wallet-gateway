package com.upbchain.pointcoin.wallet.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.upbchain.pointcoin.wallet")
@EnableScheduling
public class SpringBootJerseyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJerseyApplication.class, args);
    }
}
