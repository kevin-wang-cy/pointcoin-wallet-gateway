package com.upbchain.pointcoin.wallet.api.devonly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Profile({"development-api"})
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(SpringSecurityConfiguration.class);

    /**
     * This section defines the user accounts which can be used for
     * authentication as well as the roles each user has.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
            .withUser("user").password("123456").roles("USER").and()
            .withUser("admin").password("123456").roles("USER", "ADMIN");
        
        LOG.info("created two users: user, admin for development envivornment.");
    }

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

        http.httpBasic().and().authorizeRequests()
                .antMatchers("/api/echo/**").permitAll()
                .antMatchers("/api/mortgagewallets/**", "/api/paymentwallets/**").hasIpAddress("127.0.0.1")
                .antMatchers("/api/mortgageaccounts/**").hasRole("USER")
                .antMatchers("/api/paymentaccounts/**").hasRole("ADMIN")
                .anyRequest().fullyAuthenticated()
            .and().csrf().disable();

        // add this line to use H2 web console
        http.headers().frameOptions().disable();

        LOG.info("/api/mortgageaccounts/** needs USER role user to access.");
        LOG.info("/api/paymentaccounts/** needs ADMIN role user to access.");
    }
}
