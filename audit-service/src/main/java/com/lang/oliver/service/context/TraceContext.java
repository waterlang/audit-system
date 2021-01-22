package com.lang.oliver.service.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.lang.oliver.service.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class TraceContext {
    public static final String TRANSACTION_ID = "TransactionId";

    private static TransmittableThreadLocal<String> transactionThreadLocal = new TransmittableThreadLocal<>();
    

    public static void setTraceId(String traceId) {
        MDC.put(TRANSACTION_ID, traceId);
        transactionThreadLocal.set(traceId);
    }


    public static String getTraceId() {
        return transactionThreadLocal.get();
    }




}
