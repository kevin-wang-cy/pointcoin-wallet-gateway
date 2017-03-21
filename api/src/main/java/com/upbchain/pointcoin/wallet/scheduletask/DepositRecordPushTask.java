package com.upbchain.pointcoin.wallet.scheduletask;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
    
    @Value("${pointcoin.task.deposit-record-push.url:http://127.0.0.1:8080/api/echo}")
    private String url;
    @Value("${pointcoin.task.deposit-record-push.username:}")
    private String username;
    @Value("${pointcoin.task.deposit-record-push.password:}")
    private String password;

    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(cron = "${pointcoin.task.deposit-record-push.cron:0 0/5 * 1/1 * ?}")
    public void execute() {
        try {
            List<MortgageDepositRecord> records = mortgageService.retrieveUnSyncedMortgageDepositRecord();
            
            if (records.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No unsynced mortgage deposit record.");
                }
                return;
            }

            RestTemplate restTemplate = createRestTemplate();

            if (!StringUtils.isEmpty(this.username)) {
                restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(this.username, this.password));
            }

            Map<String, String> map = new HashMap<String, String>();
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Starting to push %d mortgage deposit records to %s", records.size(), this.url));
            }

            HttpHeaders requestHeader = new HttpHeaders();

            List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
            acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
            requestHeader.setAccept(acceptableMediaTypes);

            requestHeader.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<MortgageDepositRecord>> requestEntity = new HttpEntity<>(records, requestHeader);
            
            ResponseEntity<MortgageDepositRecordId[]> entity= restTemplate.postForEntity(url, requestEntity, MortgageDepositRecordId[].class, map);
            
            
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

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonMessageConverter.setObjectMapper(objectMapper);
        messageConverters.add(jsonMessageConverter);

        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }

}
