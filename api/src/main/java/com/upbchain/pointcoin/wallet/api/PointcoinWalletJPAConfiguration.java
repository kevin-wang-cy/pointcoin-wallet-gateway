package com.upbchain.pointcoin.wallet.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.upbchain.pointcoin.wallet.common.util.PointcoinWalletUtil;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PointcoinWalletJPAConfiguration {
	
	@Bean
	public AuditorAware<String> auditorProvider() {
		return new AuditorAwareImpl();
	}
	
	private static class AuditorAwareImpl implements AuditorAware<String> {

		/*
		 * (non-Javadoc)
		 * @see org.springframework.data.domain.AuditorAware#getCurrentAuditor()
		 */
		public String getCurrentAuditor() {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated()) {
	            return PointcoinWalletUtil.USER_POINTCOINWALLET_GATEWAY_INTERNAL;
	        }
			
			return authentication.getName();
		}
	}

}
