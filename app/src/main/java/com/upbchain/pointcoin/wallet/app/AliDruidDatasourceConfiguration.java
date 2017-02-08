package com.upbchain.pointcoin.wallet.app;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

/**
 * 
 * @author kevin.wang.cy@gmail.com
 *
 */


@Profile({"production", "default"})
@Configuration
public class AliDruidDatasourceConfiguration {
    private final static Logger LOG = LoggerFactory.getLogger(AliDruidDatasourceConfiguration.class);
    
    @Autowired
    AliDruidDatasourceProperties aliDruidDatasourceProperties;
    
    @Bean
    @Primary
    public DataSource dataSource(){
        DruidDataSource datasource = new DruidDataSource();
        
        datasource.setUrl(aliDruidDatasourceProperties.getUrl());
        datasource.setUsername(aliDruidDatasourceProperties.getUsername());
        datasource.setPassword(aliDruidDatasourceProperties.getPassword());
        datasource.setDriverClassName(aliDruidDatasourceProperties.getDriverClassName());
        
        //configuration
        datasource.setInitialSize(aliDruidDatasourceProperties.getInitialSize());
        datasource.setMinIdle(aliDruidDatasourceProperties.getMinIdle());
        datasource.setMaxActive(aliDruidDatasourceProperties.getMaxActive());
        datasource.setMaxWait(aliDruidDatasourceProperties.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(aliDruidDatasourceProperties.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(aliDruidDatasourceProperties.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(aliDruidDatasourceProperties.getValidationQuery());
        datasource.setTestWhileIdle(aliDruidDatasourceProperties.isTestWhileIdle());
        datasource.setTestOnBorrow(aliDruidDatasourceProperties.isTestOnBorrow());
        datasource.setTestOnReturn(aliDruidDatasourceProperties.isTestOnReturn());
        datasource.setPoolPreparedStatements(aliDruidDatasourceProperties.isPoolPreparedStatements());
        datasource.setMaxPoolPreparedStatementPerConnectionSize(aliDruidDatasourceProperties.getMaxPoolPreparedStatementPerConnectionSize());
        try {
            datasource.setFilters(aliDruidDatasourceProperties.getFilters());
        } catch (SQLException ex) {
            LOG.error("Exception occurs while settig Druid filters", ex);
        }
        datasource.setConnectionProperties(aliDruidDatasourceProperties.getConnectionProperties());
        
        return datasource;
    }
    
    @Bean
    FilterRegistrationBean druidWebStatFilterRegistraction() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.addUrlMappings("/*");
        servletRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico, /druid/*");
        servletRegistrationBean.setName("druidStatFilter");
        
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new WebStatFilter(), servletRegistrationBean);
        
        return registrationBean;
    }
    
    @Bean
    ServletRegistrationBean druidStatViewServletRegistraction() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new StatViewServlet());
        registrationBean.addUrlMappings("/druid/*");
        registrationBean.addInitParameter("allow", "127.0.0.1,192.168.163.1"); // IP白名单(没有配置或者为空，则允许所有访问)
        registrationBean.addInitParameter("deny", "192.168.1.73"); // IP黑名单 (存在共同时，deny优先于allow)
        registrationBean.addInitParameter("loginUsername", "admin"); // 用户名
        registrationBean.addInitParameter("loginPassword", "123456"); // 密码
        registrationBean.addInitParameter("resetEnable", "false"); // 禁用HTML页面上的“Reset All”功能
        
        registrationBean.setName("druidStatViewServlet");
        
        return registrationBean;
    }

}
