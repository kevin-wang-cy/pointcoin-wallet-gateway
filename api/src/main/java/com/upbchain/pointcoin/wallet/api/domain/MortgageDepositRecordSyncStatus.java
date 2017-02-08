package com.upbchain.pointcoin.wallet.api.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

@Embeddable
public class MortgageDepositRecordSyncStatus implements Serializable {
	private static final long serialVersionUID = -3573228151635269178L;

	@Column
	private LocalDateTime lastSyncAt;
	@Column
    private Boolean syncedWithMember;
		
	private MortgageDepositRecordSyncStatus() {
	}
	
	public static MortgageDepositRecordSyncStatus newInstance(@NotNull LocalDateTime lastSyncAt, @NotNull Boolean syncedWithMember) {
		MortgageDepositRecordSyncStatus ret = new MortgageDepositRecordSyncStatus();
		
		ret.setLastSyncAt(lastSyncAt);
		ret.setSyncedWithMember(syncedWithMember);
		
		return ret;
	}
	
	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof MortgageDepositRecordSyncStatus)) {
			return false;
		}
		MortgageDepositRecordSyncStatus it = (MortgageDepositRecordSyncStatus) o;
		
		return Objects.equals(this.getSyncedWithMember(), it.getSyncedWithMember()) && Objects.equals(this.getLastSyncAt(), it.getLastSyncAt());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getSyncedWithMember(), this.getLastSyncAt());
	}

    public LocalDateTime getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(LocalDateTime lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }

    public Boolean getSyncedWithMember() {
        return syncedWithMember;
    }

    public void setSyncedWithMember(Boolean syncedWithMember) {
        this.syncedWithMember = syncedWithMember;
    }
}