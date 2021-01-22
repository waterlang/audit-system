package com.lang.oliver.web.api.mq.product;

import com.alibaba.fastjson.JSON;
import com.lang.oliver.web.api.mq.command.BaseCommand;
import com.lang.oliver.web.api.mq.command.BaseMessageHeader;
import com.lang.oliver.web.api.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqProduct {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private static final String DEFAULT_TOPIC = "test";

    @Value("${spring.application.name}")
    private String appName;

    public void send(String jsonStrContent) {
        this.kafkaTemplate.send(DEFAULT_TOPIC, jsonStrContent);
    }


    public void sendWithTopic(String topic, String jsonStrContent) {
        this.kafkaTemplate.send(topic, jsonStrContent);
    }


    public void sendCommand(String topic, BaseCommand baseCommand){
        BaseMessageHeader baseMessageHeader = new BaseMessageHeader();
        baseMessageHeader.setTransactionId(TraceContext.getTraceId());
        baseMessageHeader.setProject(appName);

        if(baseCommand.getHeader() == null){
            baseCommand.setHeader(baseMessageHeader);
        }

        this.kafkaTemplate.send(topic, JSON.toJSONString(baseCommand));

        log.info("send kafka success:{}",JSON.toJSONString(baseCommand));
    }

}
