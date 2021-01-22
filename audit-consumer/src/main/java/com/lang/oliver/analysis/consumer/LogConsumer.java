package com.lang.oliver.analysis.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lang.oliver.analysis.consumer.event.AuditLogEvent;
import com.lang.oliver.analysis.consumer.event.EntryLogEvent;
import com.lang.oliver.analysis.domain.AuditLog;
import com.lang.oliver.analysis.domain.EntryLog;
import com.lang.oliver.analysis.dto.OperationContext;
import com.lang.oliver.analysis.repository.AuditLogRepository;
import com.lang.oliver.analysis.repository.EntryLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LogConsumer {

    @Autowired
    private EntryLogRepository entryLogRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * web入口事件
     *
     * @param
     */
    @KafkaListener(topics = "common_req_trace_log", containerFactory = "logContainerFactory")
    public void entryLogTopicListener(@Payload String content) {
        log.info("收到入口消息:{}", content);
        EntryLogEvent logInfoRequest = null;
        try {
            logInfoRequest = objectMapper.readValue(content, EntryLogEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        EntryLog entryLogInfo = new EntryLog();
        BeanUtils.copyProperties(logInfoRequest, entryLogInfo);
        entryLogRepository.insert(entryLogInfo);
    }


    /**
     * 数据库的更改：canal
     * topic :和canal里配置的topic保持一致
     *
     * @param
     */
    @KafkaListener(topics = "audit_test1", containerFactory = "logContainerFactory")
    public void auditLogTopicListener(@Payload String content) {
        log.info("收到mysql变更消息:{}", content);
        AuditLogEvent auditLogEvent = JSONObject.parseObject(content, AuditLogEvent.class);
        AuditLog auditLog = new AuditLog();
        auditLog.setDb(auditLogEvent.getDatabase());
        auditLog.setDbTable(auditLogEvent.getTable());
        auditLog.setOperationType(auditLogEvent.getType());
        auditLog.setOldData(JSON.toJSONString(auditLogEvent.getOld(), SerializerFeature.WriteMapNullValue));
        auditLog.setNewData(JSON.toJSONString(auditLogEvent.getData(), SerializerFeature.WriteMapNullValue));
        auditLog.setExecutionTime(auditLogEvent.getEs());
        auditLog.setCreateTime(new Date());
        auditLog.setDbSql(auditLogEvent.getSql());

        OperationContext operationContext = evalOperationContext(auditLogEvent.getSql());
        auditLog.setTraceId(operationContext.getTraceId());
        auditLog.setOperatorId(operationContext.getOperatorId() == null ? -1 : Integer.parseInt(operationContext.getOperatorId()));
        //存成nosql 很方便能根据id查询该条数据的所有的生命周期
        auditLog.setPrimaryIds(getUpdatedPrimaryKey(auditLogEvent.getData()));

        auditLogRepository.insert(auditLog);
    }


    private String getUpdatedPrimaryKey(List<Map<String, String>> newData) {
        List<String> primaryKeyIds = new ArrayList<>();
        if (ObjectUtils.isEmpty(newData)) {
            return null;
        }

        newData.forEach(
                k -> {
                    String id = k.get("id");
                    primaryKeyIds.add(id);
                }
        );
        return String.join(",",primaryKeyIds);
    }


    /**
     * 获取操作人及链路信息
     *
     * @param sql
     * @return
     */
    private static OperationContext evalOperationContext(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return null;
        }

        int start = sql.indexOf("/*@");
        int end = sql.indexOf("@*/");

        if (start >= 0 && end > start) {
            String[] ctx = sql.substring(start + 3, end).split(",");
            return new OperationContext(ctx[0], ctx[1]);
        }
        return new OperationContext();
    }

}
