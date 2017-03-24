package com.upbchain.pointcoin.wallet.api.resource;

import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upbchain.pointcoin.wallet.api.domain.MortgageDepositRecord;
import com.upbchain.pointcoin.wallet.api.domain.MortgageDepositRecordId;
import com.upbchain.pointcoin.wallet.api.service.HelloService;

// TODO1: List all received transactions by member's mortage address'
// TODO2: multisigature (Gate Wallet + Management Wallet <management addresss>) ---> 2 of 2 signed mortgate address

// TODO3: api for validate xpp address, this is used where member update it's recieve address (address, signed message)

// TODO4: tx out
// ---- Gate Wate pull transfer requests from Member System and generate the first layer signed tx.  (need keep the UTOX in unapporved tx to prevent double spend)
// --- Check Wallet for tx confirmation (0 - 1 DONE)
// ----04-1 Member System send request to Gate Wallet ([requestUUID, memberId, received address (verifed), count in xpp]) <-
//      -- if OK, return (requestUUID, txid) in form of first layer, a.k. Gate Way, singed transaction in form of string. State: in progress
// ----04--2, tx progress checker api ()
// ----04-3 Wallet-Backend (Large)
//

@Component
@Path("/echo")
public class HelloResource {
    private static final Logger LOG = LoggerFactory.getLogger(HelloResource.class);

    @Autowired
    private ObjectMapper objMapper;
    
    private final HelloService service;

    public HelloResource(HelloService service) {
        this.service = service;
    }

    @GET
    public String message() {
        return "Hello " + this.service.message();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response echo(@NotNull List<MortgageDepositRecord> records, @Context UriInfo uriInf) {
        
        Set<MortgageDepositRecordId> recordIds = records.stream().peek(it -> {
            try {
                LOG.info(objMapper.writeValueAsString(it));
            }
            catch (Throwable ex) {
                LOG.error(ex.getMessage(), ex);
            }
            
        }).filter(it -> {
            // as this is echo, then we only send the synced back, then an empty list will send back;
            return it.getSyncStatus().getSyncedWithMember();
        }).map(it -> it.getRecordId()).collect(toSet());
        
        return Response.ok().entity(recordIds).build();
    }

}