package com.upbchain.pointcoin.wallet.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.upbchain.pointcoin.wallet.common.PointcoinWalletClient;

@Configuration
@ApplicationPath("/api")
public class PointcoinWalletAPIConfiguration extends ResourceConfig {
    private static final Logger LOG = LoggerFactory.getLogger(PointcoinWalletAPIConfiguration.class);

    @Autowired
    public PointcoinWalletAPIConfiguration(ObjectMapper objectMapper) {
        // register endpoints
        packages("com.upbchain.pointcoin.wallet.api.resource");

        // register jackson for json
        register(new ObjectMapperContextResolver(objectMapper));
    }

    @Provider
    public static class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

        private final ObjectMapper mapper;

        public ObjectMapperContextResolver(ObjectMapper mapper) {
            this.mapper = mapper;
            /**
             * mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,
             * false); mapper.configure(SerializationFeature.INDENT_OUTPUT,
             * true); mapper.setSerializationInclusion(Include.NON_NULL);
             * mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, true);
             * mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,
             * true);
             * mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,
             * false); mapper.configure(SerializationFeature.
             * WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
             * 
             * 
             * mapper.enable(Feature.WRITE_BIGDECIMAL_AS_PLAIN);
             */

            mapper.findAndRegisterModules();

            mapper.setSerializationInclusion(Include.NON_NULL);

            mapper.enable(Feature.WRITE_BIGDECIMAL_AS_PLAIN);
            mapper.enable(SerializationFeature.INDENT_OUTPUT, SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, SerializationFeature.CLOSE_CLOSEABLE);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, SerializationFeature.WRITE_NULL_MAP_VALUES, SerializationFeature.FAIL_ON_EMPTY_BEANS);
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return mapper;
        }
    }
    
    @Bean
    public PointcoinWalletClient pointcoinWalletClient(@Qualifier("pointcoinWalletClientSettings") PointcoinWalletClientSettings settings) {
        PointcoinWalletClient client = null;
        
        if (StringUtils.isEmpty(settings.getRpcUser())) {
           client = new PointcoinWalletClient(settings.getAlias(), settings.getRpcUrl());
           if (LOG.isDebugEnabled()) {
               LOG.debug(String.format("Wallet RPC URL: %s", settings.getRpcUrl()));
           }
        }
        else {
            client = new PointcoinWalletClient(settings.getAlias(), settings.getRpcUrl(), StringUtils.trimAllWhitespace(settings.getRpcUser()), StringUtils.trimAllWhitespace(settings.getRpcPassword()));
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Wallet RPC URL: %s, RPC User: %s", settings.getRpcUrl(), settings.getRpcUser()));
            }
        }
                
        return client;
    }
}
