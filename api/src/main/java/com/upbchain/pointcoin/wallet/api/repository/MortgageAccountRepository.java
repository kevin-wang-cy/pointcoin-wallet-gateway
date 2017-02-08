package com.upbchain.pointcoin.wallet.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upbchain.pointcoin.wallet.api.domain.MortgageAccount;

public interface MortgageAccountRepository extends JpaRepository<MortgageAccount, String> {
	
}
