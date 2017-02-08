package com.upbchain.pointcoin.wallet.api.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class MortgageAccount extends AbstractAuditableEntity {
	
	@Id @Column(nullable = false, updatable = false)
	private String memberId;
	
	@Column(nullable=false)
	private String pointcoinAddress;
	
	@Column(nullable=false)
	private String alias;
	
	@Column(precision = 18, scale = 8)
	private BigDecimal recieved;

	protected MortgageAccount() {
		
	}
	
	public static MortgageAccount newInstance(@NotNull String memberId) {
		MortgageAccount account = new MortgageAccount();
		account.setMemberId(memberId);
		
		return account;
	}
	
	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(@NotNull String memberId) {
		this.memberId = memberId;
	}
	
	public String getPointcoinAddress() {
		return pointcoinAddress;
	}

	public void setPointcoinAddress(@NotNull String pointcoinAddress) {
		this.pointcoinAddress = pointcoinAddress;
	}
	
    public String getAlias() {
        return alias;
    }

    public void setAlias(@NotNull String alias) {
        this.alias = alias;
    }

	public BigDecimal getRecieved() {
		return recieved;
	}

	public void setRecieved(BigDecimal recieved) {
		this.recieved = recieved;
	}
}
