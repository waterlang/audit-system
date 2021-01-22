package com.lang.oliver.analysis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OperationContext {
    /**
     * 操作人
     */
    private String operatorId;

    /**
     * 链路跟踪 id
     */
    private String traceId;


    public OperationContext(String operatorId, String traceId) {
        this.operatorId = operatorId;
        this.traceId = traceId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
