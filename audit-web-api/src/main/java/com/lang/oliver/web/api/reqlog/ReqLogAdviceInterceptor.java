package com.lang.oliver.web.api.reqlog;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lang.oliver.web.api.trace.TraceContext;
import com.lang.oliver.web.api.util.LoginSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 请求拦截器 将入口数据写入mq 然后解析服务解析数据并写入仓储
 */
@Slf4j
public class ReqLogAdviceInterceptor implements MethodInterceptor {

    private ExecutorService fixedThreadPool;

    private KafkaTemplate kafkaTemplate;
    private ReqLogConfigurerProperties reqLogConfig;

    public ReqLogAdviceInterceptor(KafkaTemplate kafkaTemplate,ReqLogConfigurerProperties properties) {
        this.reqLogConfig = properties;
        this.kafkaTemplate = kafkaTemplate;
        this.fixedThreadPool = this.buildThreadPool(properties);
    }

    private ExecutorService buildThreadPool(ReqLogConfigurerProperties properties) {
        return new ThreadPoolExecutor(properties.getCorePoolSize(), properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(), TimeUnit.SECONDS, properties.getWorkQueue(),
                new ReqLogThreadFactory(), properties.getHandler());
    }


    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        EntryLogInfoRequest entryLogInfoRequest = this.buildEntryLogInfoRequest(methodInvocation);
        entryLogInfoRequest.setProjectName(this.reqLogConfig.getProjectName());
        Object result = methodInvocation.proceed();
        log.warn("invoke req:{}",JSON.toJSONString(entryLogInfoRequest));

        try {
            this.fixedThreadPool.execute(() -> {
                try {
                    String string = (new ObjectMapper()).writeValueAsString(entryLogInfoRequest);
                    this.kafkaTemplate.send(this.reqLogConfig.getTopicName(), string);
                } catch (JsonProcessingException var3) {
                    log.error("日志发送kafka失败,data:{}", JSON.toJSONString(entryLogInfoRequest), var3);
                }
            });
        } catch (Exception e) {
            log.error("fixedThreadPool 执行任务失败",e);
        }

        return result;
    }


    private EntryLogInfoRequest buildEntryLogInfoRequest(MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        Integer customerId = LoginSessionUtil.getLoginUserId();

        String traceId = TraceContext.getTraceId();

        return EntryLogInfoRequest.buildEntryLogInfoRequest(customerId, traceId, className, methodName);
    }

}
