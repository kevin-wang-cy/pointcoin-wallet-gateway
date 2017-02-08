package com.upbchain.pointcoin.wallet.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WalletGatewaySecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(WalletGatewaySecurityConfiguration.class);
    
    @Value("${pointcoin.api.allowip:127.0.0.1}")
    private String allowip;
    
    /**
     * This section defines the security policy for the app. - BASIC
     * authentication is supported (enough for this REST-based demo) -
     * /employees is secured using URL security shown below - CSRF headers are
     * disabled since we are only testing the REST interface, not a web one.
     *
     * NOTE: GET is not shown which defaults to permitted.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        http.authorizeRequests()
            .antMatchers("/api/**").hasRole("ADMIN")
            .anyRequest().authenticated().and().httpBasic().realmName("pointcoin-api")
            .and().csrf().disable();
        
        http.addFilterBefore(new WalletGatewayAllowedIPListFilter(this.allowip), BasicAuthenticationFilter.class);

        LOG.info(String.format("Basic authetication configruation: allowip -> %s, RealN -> pointcoin-api, Role -> ADMIN", this.allowip));
    }
    
    
}
