package com.upbchain.pointcoin.wallet.api.devonly;

import org.h2.server.web.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Profile({"development"})
@Configuration
public class H2ConsoleConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(H2ConsoleConfiguration.class);

    @Bean
    ServletRegistrationBean h2servletRegistraction() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
        registrationBean.addUrlMappings("/h2-console/*");
        registrationBean.setName("h2ConsoleServlet");
        
        if (LOG.isInfoEnabled()) {
            LOG.info("h2 database console enabled at: /h2-console");
        }
        
        return registrationBean;
    }
}
