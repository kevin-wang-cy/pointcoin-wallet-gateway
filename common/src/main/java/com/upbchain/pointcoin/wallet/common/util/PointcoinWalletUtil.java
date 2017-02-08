package com.upbchain.pointcoin.wallet.common.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */
public class PointcoinWalletUtil {
    public static final String USER_POINTCOINWALLET_GATEWAY_INTERNAL = "__pointcoinwallet-gateway-service__";
    private static final String MORTGAGE_DEPOSIT_ACCOUNT_NAME_TMPL = "(%s) Mortgage Deposit Account";
    private static final Pattern MORTGAGE_DEPOSITY_ACCOUNT_NAME_REGEX = Pattern.compile("^\\(([^\\(\\)]*)\\)\\s(Mortgage\\sDeposit\\sAccount)$");

    public static Optional<String> buildMortgageDepositAccountName(@NotNull String memberId) {
        String accountName = String.format(MORTGAGE_DEPOSIT_ACCOUNT_NAME_TMPL, memberId);

        return extraMemberIdFromMortgageAccountName(accountName).isPresent() ? Optional.of(accountName) : Optional.empty();
    }

    public static Optional<String> extraMemberIdFromMortgageAccountName(@NotNull String accounName) {
        Matcher matcher = MORTGAGE_DEPOSITY_ACCOUNT_NAME_REGEX.matcher(accounName);

        return (matcher.matches() && matcher.groupCount() == 2 && matcher.group(2).equals("Mortgage Deposit Account")) ? Optional.<String> of(matcher.group(1))
                : Optional.<String> empty();
    }
}