package com.lang.oliver.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


/***
 * 入口来源
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "entry_log")
public class EntryLog {
    @Id
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 登录人信息
     */
    @Column(name = "customer_id")
    private Integer customerId;
    /**
     * 每次请求产生的traceId
     */
    @Column(name = "trace_id")
    private String traceId;
    @Column(name = "project_name")
    private String projectName;
    @Column(name = "class_name")
    private String className;
    @Column(name = "method_name")
    private String methodName;
    @Column(name = "request_time")
    private Date requestTime;

    private String parameters;

    /**
     * 返回结果  该值需要根据不同的情况来判断要不要记录，比如返回数据比较大（导出操作）会严重影响性能
     */
    private String result;
    @Column(name = "create_time")
    private Date createTime;

}
