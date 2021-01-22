package com.lang.oliver.web.api.config;

import com.lang.oliver.web.api.filter.TraceFilter;
import com.lang.oliver.web.api.reqlog.ReqLogAdviceInterceptor;
import com.lang.oliver.web.api.reqlog.ReqLogConfigurerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class CommonConfig {

    @Value("${point.cut:com.lang.oliver.web.api.controller}")
    private String pointCut;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new TraceFilter());
        registration.addUrlPatterns("/*");
        registration.setName("TraceFilter");
        registration.setOrder(-100);
        return registration;
    }


    /**
     * @return
     */
    @Bean
    public ReqLogConfigurerProperties packageReqLofConfig() {
        ReqLogConfigurerProperties reqLofConfig = new ReqLogConfigurerProperties();
        reqLofConfig.setProjectName("k2-tmk-web-api");
        reqLofConfig.setTopicName("common_req_trace_log");
        return reqLofConfig;
    }


    @Bean
    public AspectJExpressionPointcutAdvisor configurabledvisor(ReqLogConfigurerProperties reqLogConfigurer) {
        String pointCutStr = "execution(public * " + pointCut + "..*.*(..))";
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(pointCutStr);
        advisor.setAdvice(new ReqLogAdviceInterceptor(kafkaTemplate,reqLogConfigurer));
        return advisor;
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
