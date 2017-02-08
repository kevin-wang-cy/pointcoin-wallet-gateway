package com.upbchain.pointcoin.wallet.api.repository;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upbchain.pointcoin.wallet.api.domain.MortgageDepositRecord;
import com.upbchain.pointcoin.wallet.api.domain.MortgageDepositRecordId;

public interface MortgageDepositRecordResposity extends JpaRepository<MortgageDepositRecord, MortgageDepositRecordId> {
	public List<MortgageDepositRecord> findByRecordIdMemberId(@NotNull String memberId);
	
	public List<MortgageDepositRecord> findByRecordIdMemberIdAndRecordIdTxId(@NotNull String memberId, @NotNull String txId);
	
	public List<MortgageDepositRecord> findByRecordIdTxId(@NotNull String txId);
	
	public List<MortgageDepositRecord> findByRecordIdMemberIdAndSyncStatusSyncedWithMemberFalse(@NotNull String memberId);
	
	public List<MortgageDepositRecord> findBySyncStatusSyncedWithMemberFalse();
}
