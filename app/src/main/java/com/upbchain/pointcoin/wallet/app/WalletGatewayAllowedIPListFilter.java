package com.upbchain.pointcoin.wallet.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */

public class WalletGatewayAllowedIPListFilter extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(WalletGatewayAllowedIPListFilter.class);

    private final String allowip;
    private final List<IpAddressMatcher> ipAddressMatchers = new ArrayList<IpAddressMatcher>();;

    public WalletGatewayAllowedIPListFilter(@NotNull String allowip) {
        this.allowip = allowip;
        Arrays.asList(allowip.split(";")).stream().filter(it -> !StringUtils.isEmpty(it)).forEach(it -> {
            ipAddressMatchers.add(new IpAddressMatcher(it.trim()));
        });
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String remoteAddr = request.getRemoteAddr();

        if (this.ipAddressMatchers.stream().anyMatch(it -> it.matches(remoteAddr))) {
            chain.doFilter(request, response);

            return;
        }
        
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Reject access from remote address %s due to not comply with allow ip setting: %s", remoteAddr, this.allowip));
        }
    }

}
