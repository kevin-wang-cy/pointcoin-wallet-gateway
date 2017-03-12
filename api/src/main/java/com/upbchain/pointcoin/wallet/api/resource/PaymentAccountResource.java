package com.upbchain.pointcoin.wallet.api.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


// TODO4: tx out
// ---- Gate Wate pull transfer requests from Member System and generate the first layer signed tx.  (need keep the UTOX in unapporved tx to prevent double spend)
// --- Check Wallet for tx confirmation (0 - 1 DONE)
// ----04-1 Member System send request to Gate Wallet ([requestUUID, memberId, received address (verifed), count in xpp]) <-
//      -- if OK, return (requestUUID, txid) in form of first layer, a.k. Gate Way, singed transaction in form of string. State: in progress
// ----04--2, tx progress checker api ()
// ----04-3 Wallet-Backend (Large)
//

@Component
@Path("/{parameter: paymentaccounts|paymentwallets}")
public class PaymentAccountResource {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentAccountResource.class);

    @GET
    public String message() {
        return "Hello PaymentAccount";
    }
    
}