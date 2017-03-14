package com.upbchain.pointcoin.wallet.app;

import com.ulisesbocchio.jasyptspringboot.resolver.DefaultPropertyResolver;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */

@Profile({ "production", "test" })
@Configuration
public class JasyptEncryptionConfiguration {
    private final static Logger LOG = LoggerFactory.getLogger(JasyptEncryptionConfiguration.class);
    
    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(@Value("${jasypt.encryptor.password}") String password) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPasswordCharArray((new StringBuffer()).append("POINTCOIN[").append(password).append("]").toString().toCharArray());
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        
        encryptor.setConfig(config);
        
        return encryptor;
    }
    
    @Bean(name="encryptablePropertyResolver")
    public EncryptablePropertyResolver encryptablePropertyResolver(@Autowired @Qualifier("jasyptStringEncryptor") StringEncryptor stringEncryptor) {
        return new JasyptEncryptablePropertyResolver(stringEncryptor);
    }
    
    private class JasyptEncryptablePropertyResolver extends DefaultPropertyResolver implements EncryptablePropertyResolver {

        private final StringEncryptor encryptor;

        public JasyptEncryptablePropertyResolver(StringEncryptor encryptor) {
            super(encryptor);

            this.encryptor = encryptor;
        }

        @Override
        public String resolvePropertyValue(String value) {
            return super.resolvePropertyValue(value);
        }
    }

}
