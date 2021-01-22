package com.lang.oliver.service.util;

import javax.servlet.http.HttpServletRequest;

public class TraceIdUtil {

    public static final String TRACE_ID = "TransactionId";

    /**
     *
     * @return
     */
    public static String getTraceIdFromRequest(HttpServletRequest request){
        return request.getHeader(TRACE_ID);
    }



}
