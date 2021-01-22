package com.lang.oliver.web.api.util;

import java.util.UUID;

public class TraceIdUtil {

    /**
     *
     * @return
     */
    public static String getTraceId(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
