package com.lang.oliver.web.api.reqlog;

import lombok.Data;

import java.util.Date;

@Data
public class EntryLogInfoRequest {

    /**
     * 登录人信息
     */
    private Integer customerId;
    /**
     * 每次请求产生的traceId
     */
    private String traceId;
    private String projectName;
    private String className;
    private String methodName;
    private Date requestTime;
    private String parameter;

    /**
     * 返回结果  该值需要根据不同的情况来判断要不要记录，比如返回数据比较大（导出操作）会严重影响性能
     */
    private String result;

    public EntryLogInfoRequest() {
    }

    public static EntryLogInfoRequest buildEntryLogInfoRequest(Integer customerId, String traceId, String className,
                                                               String methodName) {
        EntryLogInfoRequest entryLogInfoRequest = new EntryLogInfoRequest();
        entryLogInfoRequest.setCustomerId(customerId);
        entryLogInfoRequest.setTraceId(traceId);
        entryLogInfoRequest.setClassName(className);
        entryLogInfoRequest.setMethodName(methodName);
        entryLogInfoRequest.setRequestTime(new Date());
        return entryLogInfoRequest;
    }



}
