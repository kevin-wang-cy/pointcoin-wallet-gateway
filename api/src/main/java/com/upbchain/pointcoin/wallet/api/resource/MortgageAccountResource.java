package com.upbchain.pointcoin.wallet.api.resource;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.upbchain.pointcoin.wallet.api.domain.MortgageAccount;
import com.upbchain.pointcoin.wallet.api.domain.MortgageDepositRecord;
import com.upbchain.pointcoin.wallet.api.resource.util.ErrorMessage;
import com.upbchain.pointcoin.wallet.api.resource.util.Http5XXStatusExtension;
import com.upbchain.pointcoin.wallet.api.service.MortgageAccountAlreadyExistException;
import com.upbchain.pointcoin.wallet.api.service.MortgageAccountNotExistException;
import com.upbchain.pointcoin.wallet.api.service.MortgageAccountViolationException;
import com.upbchain.pointcoin.wallet.api.service.MortgageService;
import com.upbchain.pointcoin.wallet.api.service.MortgageService.BootTransactionIntoAccountResult;
import com.upbchain.pointcoin.wallet.api.service.MortgageTransactionInvalidException;
import com.upbchain.pointcoin.wallet.api.service.MortgageTransactionNotExistException;
import com.upbchain.pointcoin.wallet.common.InvalidPointcoinWalletMortgageMemberException;
import com.upbchain.pointcoin.wallet.common.PointcoinTransaction;
import com.upbchain.pointcoin.wallet.common.PointcoinWalletRPCException;

@Path("/mortgageaccounts")
public class MortgageAccountResource {
    private static final Logger LOG = LoggerFactory.getLogger(MortgageAccountResource.class);
    
    @Autowired
    private MortgageService mortgageService;
    
    @POST
    @Path("/transactions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookPointcoinTransactionIntoAccount(@NotNull PointcoinTransaction transaction, @Context UriInfo uriInfo){
        try {
            BootTransactionIntoAccountResult result = mortgageService.bookPointcoinTransactionIntoAccount(transaction.getTxId(), Optional.<String>empty());
            return Response.created(uriInfo.getAbsolutePathBuilder().path(transaction.getTxId()).build()).entity(result).build();
        } 
        catch (MortgageTransactionNotExistException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.newInstance().status(Response.Status.NOT_FOUND.getStatusCode()).message("Transaction doesn't exist.").entity(ex.getPointcoinTransaction().getTxId())).encoding("UTF-8").build();
        } 
        catch (MortgageTransactionInvalidException ex) {
            return Response.status(Response.Status.PRECONDITION_FAILED).entity(ErrorMessage.newInstance().status(Response.Status.PRECONDITION_FAILED.getStatusCode()).message(ex.getMessage()).entity(ex.getPointcoinTransaction())).build();
        }
        catch (PointcoinWalletRPCException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ErrorMessage.newInstance().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).message(ex.getCause().getMessage())).encoding("UTF-8").build();
        }
    }
    
    @GET
    @Path("/transactions/{txId}")
    @Produces(MediaType.APPLICATION_JSON) 
    public Response retrieveMemberDepositTx(@NotNull @PathParam("txId") String txId ){
        List<MortgageDepositRecord> records = mortgageService.retrieveMortgageDepositRecord(txId);
        
        if (records == null || records.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.newInstance().status(Response.Status.NOT_FOUND.getStatusCode()).message("Transaction doesn't exist.").entity(txId)).encoding("UTF-8").build();
        }
        
        return  Response.ok(records).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMemberMortgageAccount(@NotNull MortgageAccount account, @Context UriInfo uriInfo) {
        try {
            MortgageAccount accountCreated = mortgageService.createMortgageAccount(account.getMemberId());
            
            return Response.created(uriInfo.getAbsolutePathBuilder().path(accountCreated.getMemberId()).build()).entity(accountCreated).build();
        }
        catch (InvalidPointcoinWalletMortgageMemberException ex) {
            return Response.status(Response.Status.PRECONDITION_FAILED).entity(ErrorMessage.newInstance().status(Response.Status.PRECONDITION_FAILED.getStatusCode()).message(ex.getMessage()).entity(ex.getMemberId())).build();
        }
        catch (MortgageAccountAlreadyExistException ex) {
            return Response.status(Response.Status.CONFLICT).contentLocation(uriInfo.getAbsolutePathBuilder().path(ex.getMortgageAccount().get().getMemberId()).build()).entity(ErrorMessage.newInstance().status(Response.Status.CONFLICT.getStatusCode()).message("Member's deposit account had already created before.").entity(ex.getMortgageAccount())).build();
        }            
        catch (PointcoinWalletRPCException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ErrorMessage.newInstance().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).message(ex.getCause().getMessage())).encoding("UTF-8").build();
        }
    }
    
    @GET
    @Path("/{memberId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMemberMortgageAccount(@PathParam("memberId") String memberId) {
        
        try {
            MortgageAccount account =  mortgageService.retrieveMortgageAccount(memberId);
            
            return Response.ok(account).build();
        }
        catch (MortgageAccountNotExistException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.newInstance().status(Response.Status.NOT_FOUND.getStatusCode()).message("Account doesn't exist.").entity(ex.getMortgageAccount().get())).encoding("UTF-8").build();
        } 
        catch (MortgageAccountViolationException ex) {          
            return Response.status(Http5XXStatusExtension.DATA_VIOLATION).entity(ErrorMessage.newInstance().status(Http5XXStatusExtension.DATA_VIOLATION.getStatusCode()).message("Member deposit account between DB and wallet isn't consistent with each other.").entity(ex.getMortgageAccount().get())).encoding("UTF-8").build();
        }
        catch (PointcoinWalletRPCException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ErrorMessage.newInstance().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).message(ex.getCause().getMessage())).encoding("UTF-8").build();
        }
    }
    
    @PUT
    @Path("/{memberId}/transactions")
    @Produces(MediaType.APPLICATION_JSON) 
    public Response syncLatestPointcoinTransactionIntoAccount(@PathParam("memberId") String memberId, @DefaultValue("24") @QueryParam("latest") int latestN){
        try {
            List<MortgageDepositRecord> records = mortgageService.syncLatestPointcoinTransactionIntoAccount(memberId, latestN);
            return Response.ok(records).build();
        }
        catch (MortgageAccountNotExistException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.newInstance().status(Response.Status.NOT_FOUND.getStatusCode()).message("Account doesn't exist.").entity(ex.getMortgageAccount().get())).encoding("UTF-8").build();
        } 
        catch (InvalidPointcoinWalletMortgageMemberException ex) {
            return Response.status(Response.Status.PRECONDITION_FAILED).entity(ErrorMessage.newInstance().status(Response.Status.PRECONDITION_FAILED.getStatusCode()).message(ex.getMessage()).entity(ex.getMemberId())).encoding("UTF-8").build();
        }
        catch (MortgageAccountViolationException ex) {   
            return Response.status(Http5XXStatusExtension.DATA_VIOLATION).entity(ErrorMessage.newInstance().status(Http5XXStatusExtension.DATA_VIOLATION.getStatusCode()).message("Member deposit account between DB and wallet isn't consistent with each other.").entity(ex.getMortgageAccount().get())).encoding("UTF-8").build();
        }
        catch (PointcoinWalletRPCException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ErrorMessage.newInstance().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).message(ex.getCause().getMessage())).encoding("UTF-8").build();
        }
    }
    
    @POST
    @Path("/{memberId}/transactions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookPointcoinTransactionIntoAccount(@PathParam("memberId") String memberId, @NotNull PointcoinTransaction transaction, @Context UriInfo uriInfo){
        try {
            BootTransactionIntoAccountResult result = mortgageService.bookPointcoinTransactionIntoAccount(transaction.getTxId(), Optional.of(memberId));
            return Response.created(uriInfo.getAbsolutePathBuilder().path(transaction.getTxId()).build()).entity(result).build();
        } 
        catch (MortgageTransactionNotExistException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.newInstance().status(Response.Status.NOT_FOUND.getStatusCode()).message("Transaction doesn't exist.").entity(transaction.getTxId())).encoding("UTF-8").build();
        } 
        catch (MortgageTransactionInvalidException ex) {
            return Response.status(Response.Status.PRECONDITION_FAILED).entity(ErrorMessage.newInstance().status(Response.Status.PRECONDITION_FAILED.getStatusCode()).message(ex.getMessage()).entity(ex.getPointcoinTransaction())).build();
        }
        catch (PointcoinWalletRPCException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ErrorMessage.newInstance().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).message(ex.getCause().getMessage())).encoding("UTF-8").build();
        }
    }
    
    @GET
    @Path("/{memberId}/transactions")
    @Produces(MediaType.APPLICATION_JSON) 
    public List<MortgageDepositRecord> retrieveMemberDepositTx(@PathParam("memberId") String memberId, @DefaultValue("false") @QueryParam("unsynced") boolean unsynced) {
       return unsynced ? mortgageService.retrieveMemberUnSyncedMortgageDepositRecord(memberId) : mortgageService.retrieveMemberMortgageDepositRecord(memberId);
    }
    
    @GET
    @Path("/{memberId}/transactions/{txId}")
    @Produces(MediaType.APPLICATION_JSON) 
    public Response retrieveMemberDepositTx(@PathParam("memberId") String memberId, @PathParam("txId") String txId ) {
        List<MortgageDepositRecord> records = mortgageService.retrieveMemberMortgageDepositRecord(memberId, txId);
        
        if (records == null || records.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.newInstance().status(Response.Status.NOT_FOUND.getStatusCode()).message("Transaction doesn't exist.").entity(txId)).encoding("UTF-8").build();
        }
        
        return  Response.ok(records).build();
    }
    
}
