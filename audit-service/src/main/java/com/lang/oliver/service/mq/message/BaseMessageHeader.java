package com.lang.oliver.service.mq.message;

import java.util.HashMap;

public class BaseMessageHeader extends HashMap<String, String> {
    public void setIp(String ip) {
        this.put("ip", ip);
    }

    public void setProject(String project) {
        this.put("project", project);
    }

    public void setTransactionId(String transactionId) {
        this.put("TransactionId", transactionId);
    }

    public void setUserId(String userId) {
        this.put("userId", userId);
    }

    public String getIp() {
        return this.get("ip");
    }

    public String getProject() {
        return this.get("project");
    }

    public String getTraceId() {
        return this.get("traceId");
    }

    public String getUserId() {
        return this.get("userId");
    }
}
