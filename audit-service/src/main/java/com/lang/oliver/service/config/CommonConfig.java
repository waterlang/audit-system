package com.lang.oliver.service.config;

import com.lang.oliver.service.filter.AuditCDCLogInterceptor;
import com.lang.oliver.service.filter.TraceFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CommonConfig {

    @Bean
    public AuditCDCLogInterceptor auditLogInterceptor() {
        AuditCDCLogInterceptor sqlStatsInterceptor = new AuditCDCLogInterceptor();
        Properties properties = new Properties();
        properties.setProperty("auditTables", "crm.*");
        sqlStatsInterceptor.setProperties(properties);
        return sqlStatsInterceptor;
    }


    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new TraceFilter());
        registration.addUrlPatterns("/*");
        registration.setName("TraceFilter");
        registration.setOrder(-100);
        return registration;
    }

}
