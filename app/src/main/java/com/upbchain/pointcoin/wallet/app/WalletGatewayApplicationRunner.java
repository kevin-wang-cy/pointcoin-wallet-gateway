package com.upbchain.pointcoin.wallet.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class WalletGatewayApplicationRunner implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(WalletGatewayApplicationRunner.class);
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug(args.toString());
        }
    }
}
