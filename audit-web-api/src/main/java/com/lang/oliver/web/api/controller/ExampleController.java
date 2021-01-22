package com.lang.oliver.web.api.controller;


import com.alibaba.fastjson.JSON;
import com.lang.oliver.web.api.mq.command.UpdateUserCommand;
import com.lang.oliver.web.api.mq.product.MqProduct;
import com.lang.oliver.web.api.trace.TraceContext;
import com.lang.oliver.web.api.util.TraceIdUtil;
import com.lang.oliver.web.api.vo.WebApiUserVo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/user")
public class ExampleController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MqProduct mqProduct;

    @Value("${biz.user.topic}")
    private String userTopic;

    @PutMapping("")
    public String saveUser(@RequestBody WebApiUserVo userVo) {
        log.info("userVo:{}", JSON.toJSONString(userVo));

        HttpHeaders headers = new HttpHeaders();
        headers.set(TraceContext.TRANSACTION_ID, MDC.get(TraceContext.TRANSACTION_ID));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        HttpEntity httpEntity = new HttpEntity<>(JSON.toJSONString(userVo), headers);

        Integer userId = restTemplate.exchange("http://127.0.0.1:9999/user", HttpMethod.POST,
                httpEntity, Integer.class).getBody();
        return String.valueOf(userId);
    }


    /**
     * 更新user 走mq方式
     *
     * @param userVo
     * @return
     */
    @PostMapping("")
    public String updateUser(@RequestBody WebApiUserVo userVo) {
        log.info("updateUser userVo:{}", JSON.toJSONString(userVo));

        UpdateUserCommand command =  buildUpdateUserCommand(userVo);
        mqProduct.sendCommand(userTopic,command);
        return "success";
    }


    private UpdateUserCommand buildUpdateUserCommand(WebApiUserVo vo) {
        String uuid = TraceIdUtil.getTraceId();
        UpdateUserCommand.UpdateUser updateUser = new UpdateUserCommand
                .UpdateUser(vo.getId(), vo.getName(), vo.getAddress());

        return new UpdateUserCommand(uuid, new Date().getTime(),
                UpdateUserCommand.class.getSimpleName(), updateUser, null);
    }



}
