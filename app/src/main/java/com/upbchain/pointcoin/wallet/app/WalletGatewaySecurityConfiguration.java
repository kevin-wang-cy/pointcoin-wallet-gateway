package com.upbchain.pointcoin.wallet.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
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

    @Autowired
    private WalletGatewaySecurityProperties walletGatewaySecurityProperties;

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
                .antMatchers("/api/echo/**").permitAll()
                .antMatchers("/api/mortgagewallets/**", "/api/paymentwallets/**").hasIpAddress("127.0.0.1")
                .antMatchers("/api/wallet/**").hasAnyRole("PARTNER", "USER")
                .antMatchers("/api/mortgageaccounts/**").hasRole("USER")
                .antMatchers("/api/paymentaccounts/**").hasRole("ADMIN")
                .anyRequest().fullyAuthenticated();

        http.httpBasic().realmName("pointcoin-api")
                .and().addFilterBefore(new WalletGatewayAllowedIPListFilter(walletGatewaySecurityProperties.getAllowip()), BasicAuthenticationFilter.class)
                .csrf().disable();

        LOG.info(String.format("Basic authetication configruation: allowip -> %s, RealN -> pointcoin-api", walletGatewaySecurityProperties.getAllowip()));
    }

    /**
     * This section defines the user accounts which can be used for
     * authentication as well as the roles each user has.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryUserDetailsManagerConfigurer = auth.inMemoryAuthentication();

        walletGatewaySecurityProperties.getUsers().forEach(user -> {
            inMemoryUserDetailsManagerConfigurer.withUser(user.getName()).password(user.getPassword()).roles(user.getRoles().toArray(new String[0]));
        });

        LOG.info(String.format("created %s users per pointcoin security configuration.", walletGatewaySecurityProperties.getUsers().size()));
    }
}
