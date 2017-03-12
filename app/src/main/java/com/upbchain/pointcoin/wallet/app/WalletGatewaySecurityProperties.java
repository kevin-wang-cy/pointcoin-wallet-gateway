package com.upbchain.pointcoin.wallet.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author kevin.wang.cy@gmail.com
 */
@ConfigurationProperties(prefix="pointcoin.security")
@Component
public class WalletGatewaySecurityProperties {

    private String allowip = "127.0.0.1";
    private List<User> users = new ArrayList<>();

    public String getAllowip() {
        return allowip;
    }

    public void setAllowip(String allowip) {
        this.allowip = allowip;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public static class User {
        private String name;
        private String password;
        private List<String> roles;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
