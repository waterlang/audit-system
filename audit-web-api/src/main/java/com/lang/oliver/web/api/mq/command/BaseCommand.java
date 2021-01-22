package com.lang.oliver.web.api.mq.command;

import lombok.Data;

@Data
public class BaseCommand<T,U> {

    /**
     * 每个事件的唯一id
     */
    protected String id;

    /**
     * 事件产生的时间
     */
    protected Long createTime;

    /**
     * 自定义key.事件key,一般做为事件类型处理
     */
    protected String key;
    /**
     * 事件版本号.
     */
    protected String version = "1.0.0";

    /**
     * 具体的载体
     */
    protected T payload;


    /**
     * 头部信息：比如 消息来源位置（项目，ip等）
     */
    protected U header;
}
