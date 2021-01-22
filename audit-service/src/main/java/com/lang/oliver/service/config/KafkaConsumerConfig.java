package com.lang.oliver.service.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {


    @Value("${kafka.log.servers:127.0.0.1:9092}")
    private String kafkaServers;
    @Value("${kafka.log.consumer.group.id:audit-service}")
    private String consumerGroupId;

    @Value("${kafka.consumer.auto.offset.reset:latest}")
    private String  autoOffsetReset;
    @Value("${kafka.consumer.enable.auto.commit:true}")
    private String enableAutoCommit;
    @Value("${kafka.consumer.concurrency:3}")
    private int concurrency;


    /**
     *
     * @return
     */
    @Bean("logContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
    logContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerLogFactory());
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setPollTimeout(1500);
        return factory;
    }


    /**
     *
     * @return
     */
    public ConsumerFactory<String, String> consumerLogFactory() {
        return getConsumerFactory(kafkaServers, consumerGroupId);
    }

    private ConsumerFactory<String, String> getConsumerFactory(String logServers, String logConsumerGroupId) {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, logServers);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, logConsumerGroupId);
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return new DefaultKafkaConsumerFactory<>(propsMap);
    }

}
