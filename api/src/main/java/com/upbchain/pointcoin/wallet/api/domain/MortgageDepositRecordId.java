package com.upbchain.pointcoin.wallet.api.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class MortgageDepositRecordId implements Serializable {
	private static final long serialVersionUID = -3573228151635269178L;

	@Column(updatable = false, nullable = false)
	private String memberId;
	
	@Column(updatable = false, nullable = false)
	private String txId;
	
	@Column(updatable = false, nullable = false)
	private String pointcoinAddress;
	
	
	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}
	
	public String getPointcoinAddress() {
		return pointcoinAddress;
	}

	public void setPointcoinAddress(String pointcoinAddress) {
		this.pointcoinAddress = pointcoinAddress;
	}

	private MortgageDepositRecordId() {
	}
	
	public static MortgageDepositRecordId newInstance(@NotNull String memberId, @NotNull String txId, @NotNull String pointcoinAddress) {
		MortgageDepositRecordId ret = new MortgageDepositRecordId();
		
		ret.setMemberId(memberId);
		ret.setTxId(txId);
		ret.setPointcoinAddress(pointcoinAddress);
		
		return ret;
	}
	
	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof MortgageDepositRecordId)) {
			return false;
		}
		MortgageDepositRecordId it = (MortgageDepositRecordId) o;
		
		return Objects.equals(this.getMemberId(), it.getMemberId()) && Objects.equals(this.getPointcoinAddress(), it.getPointcoinAddress()) && Objects.equals(this.getTxId(), it.getTxId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getMemberId(), this.getPointcoinAddress(), this.getTxId());
	}
}