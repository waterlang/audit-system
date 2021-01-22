package com.lang.oliver.web.api.trace;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.lang.oliver.web.api.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class TraceContext {
    public static final String TRANSACTION_ID = "TransactionId";

    private static TransmittableThreadLocal<String> transactionThreadLocal = new TransmittableThreadLocal<>();

    public static void setTraceId() {
        String traceId = TraceIdUtil.getTraceId();
        MDC.put(TRANSACTION_ID, traceId);
        transactionThreadLocal.set(traceId);
    }


    public static String getTraceId() {
        return transactionThreadLocal.get();
    }

}
