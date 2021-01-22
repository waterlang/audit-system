package com.lang.oliver.web.api.reqlog;

import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *  拦截相关的配置信息
 *  优化：可以修改读取配置文件
 */

@Data
public class ReqLogConfigurerProperties {

    private String projectName;
    private String topicName = "common_req_trace_log";
    private int corePoolSize = 5;
    private int maximumPoolSize = 10;
    private long keepAliveTime = 60L;
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque(5000);
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();

}
