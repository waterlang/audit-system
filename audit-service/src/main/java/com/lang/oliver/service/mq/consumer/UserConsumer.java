package com.lang.oliver.service.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.lang.oliver.service.context.TraceContext;
import com.lang.oliver.service.domain.User;
import com.lang.oliver.service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserConsumer {

    @Autowired
    private UserRepository userRepository;

    /**
     * 数据库的更改：canal 走的业务kafka
     *
     * @param
     */
    @KafkaListener(topics = "${biz.user.topic}", containerFactory = "logContainerFactory")
    public void auditLogTopicListener(@Payload String content) {
        log.info("收到更新user消息:{}", content);
        JSONObject jsonObject = JSONObject.parseObject(content);

        setTraceIdToContext(jsonObject);

        User user = buildUser(jsonObject);
        userRepository.updateById(user);
    }

    private User buildUser(JSONObject jsonObject) {
        JSONObject playLoad = (JSONObject) jsonObject.get("payload");

        User user = new User();
        user.setAddress(playLoad.getString("address"));
        user.setId(playLoad.getInteger("id"));
        user.setName(playLoad.getString("name"));
        return user;
    }


    /**
     * 可以使用aop统一拦截处理
     * 不然每个地方都要写一次
     */
    private void setTraceIdToContext(JSONObject jsonObject) {
        JSONObject header = (JSONObject) jsonObject.get("header");
        TraceContext.setTraceId((String) header.get("TransactionId"));
    }

}
