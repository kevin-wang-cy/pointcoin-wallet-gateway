package com.upbchain.pointcoin.wallet.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Optional;

import org.junit.Test;

public class PointcoinWalletUtilTest {

    @Test
    public void testBuildMortgageDeposityAccountName() {
        String tmpl = "(%s) Mortgage Deposit Account";
        String memberId = "xxxx-xxxx-xxxx-xxxx";
        assertEquals(Optional.<String> of(String.format(tmpl, memberId)), PointcoinWalletUtil.buildMortgageDepositAccountName(memberId));

        memberId = "should not have ( in memberId";
        assertEquals(Optional.<String> empty(), PointcoinWalletUtil.buildMortgageDepositAccountName(memberId));

        memberId = "should not have ) in memberId";
        assertEquals(Optional.<String> empty(), PointcoinWalletUtil.buildMortgageDepositAccountName(memberId));

        memberId = "validate letter in memberid: `~!@#$%^&*-_+=[]{}|\\;':\",.<>/?";
        assertEquals(Optional.<String> of(String.format(tmpl, memberId)), PointcoinWalletUtil.buildMortgageDepositAccountName(memberId));
    }

    @Test
    public void testExtraMemberIdFromMortgageAccountName() {
        String memberId = "xxxx-xxxx-xxxx-xxxx";

        assertEquals(Optional.<String> of(memberId),
                PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(PointcoinWalletUtil.buildMortgageDepositAccountName(memberId).get()));

        memberId = "kevin.wang@gmail.com";
        assertEquals(Optional.<String> of(memberId),
                PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(PointcoinWalletUtil.buildMortgageDepositAccountName(memberId).get()));

        memberId = "`validate letter in memberid: ~!@#$%^&*-_+=[]{}|\\;':\",.<>/?";
        assertEquals(Optional.<String> of(memberId),
                PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(PointcoinWalletUtil.buildMortgageDepositAccountName(memberId).get()));

        String accountName = "(case sensitive) mortgage deposit account";
        assertFalse(PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(accountName).isPresent());

        accountName = "(should not have ( in memberId) Mortgage Deposit Account";
        assertEquals(Optional.<String> empty(), PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(accountName));

        accountName = "(should not have ) in memberId) Mortgage Deposit Account";
        assertEquals(Optional.<String> empty(), PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(accountName));

        accountName = "in validate format";
        assertFalse(PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(accountName).isPresent());

        accountName = " " + PointcoinWalletUtil.buildMortgageDepositAccountName("no space before and afer account") + " ";
        assertFalse(PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(accountName).isPresent());

        accountName = PointcoinWalletUtil.buildMortgageDepositAccountName("not support contact two accounts").get()
                + PointcoinWalletUtil.buildMortgageDepositAccountName("not support contact two accounts").get();
        assertFalse(PointcoinWalletUtil.extraMemberIdFromMortgageAccountName(accountName).isPresent());

    }

}