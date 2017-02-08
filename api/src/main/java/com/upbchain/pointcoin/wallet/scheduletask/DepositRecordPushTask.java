package com.upbchain.pointcoin.wallet.scheduletask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.upbchain.pointcoin.wallet.api.domain.MortgageDepositRecord;
import com.upbchain.pointcoin.wallet.api.domain.MortgageDepositRecordId;
import com.upbchain.pointcoin.wallet.api.service.MortgageService;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */

@Component
public class DepositRecordPushTask {

    private static final Logger LOG = LoggerFactory.getLogger(DepositRecordPushTask.class);

    @Autowired
    private MortgageService mortgageService;
    
    @Value("${pointcoin.task.deposit-record-push.url:http://localhost:8080/api/echo}")
    private String url;
    
    @Scheduled(cron = "${pointcoin.task.deposit-record-push.cron:0 0/15 * 1/1 * ?}")
    public void execute() {
        try {
            List<MortgageDepositRecord> records = mortgageService.retrieveUnSyncedMortgageDepositRecord();
            
            if (records.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No unsynced mortgage deposit record.");
                }
                return;
            }
            
            
            RestTemplate restTemplate = new RestTemplate();
            
            Map<String, String> map = new HashMap<String, String>();
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Starting to push %d mortgage deposit records to %s", records.size(), this.url));
            }
            
            
            ResponseEntity<MortgageDepositRecordId[]> entity= restTemplate.postForEntity(url, records, MortgageDepositRecordId[].class, map);
            
            
            if (entity.getStatusCodeValue() == 200) {
                MortgageDepositRecordId[] recordIds = entity.getBody();
                
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("Received %d mortgage deposit records which should have been synced with %s, start to mark it as synced", recordIds.length, this.url));
                }
               
                
                if (recordIds != null) {
                    mortgageService.markMortgageDepositRecordAsSynced(Arrays.asList(recordIds));
                }
                
                if (LOG.isInfoEnabled()) {
                    LOG.info(String.format("Push OK: sent %d Records, received %d Ids", records.size(), recordIds.length));
                }
            }
            else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(String.format("Push Failed: status: %d, reason: %s", entity.getStatusCodeValue(), entity.getStatusCode().getReasonPhrase()));
                }
            }
        }
        catch (RestClientException ex) {
            LOG.warn("Push Failed:", ex);
        }
        catch (Throwable ex) {
            LOG.warn("Push Failed:", ex);
        }
    }

}
