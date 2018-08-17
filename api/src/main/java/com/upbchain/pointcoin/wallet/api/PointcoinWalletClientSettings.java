package com.upbchain.pointcoin.wallet.api;

import java.net.URL;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component("pointcoinWalletClientSettings")
@ConfigurationProperties(prefix="pointcoin.wallet")
public final class PointcoinWalletClientSettings {
    @NotNull
    private URL rpcUrl;

    private String kind;
    private String alias;
    private String rpcUser;
    private String rpcPassword;
    public URL getRpcUrl() {
        return rpcUrl;
    }
    public void setRpcUrl(URL serviceUrl) {
        this.rpcUrl = serviceUrl;
    }
    public String getRpcUser() {
        return rpcUser == null ? "" : rpcUser.trim();
    }
    public void setRpcUser(String username) {
        this.rpcUser = username;
    }
    public String getRpcPassword() {
        return rpcPassword == null ? "" : rpcPassword.trim();
    }
    public void setRpcPassword(String password) {
        this.rpcPassword = password;
    }
    public String getAlias() {
        return StringUtils.isEmpty(alias) ? "default" : alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public String getKind() {
        return StringUtils.isEmpty(kind) ? "pointcoin" : kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
}
