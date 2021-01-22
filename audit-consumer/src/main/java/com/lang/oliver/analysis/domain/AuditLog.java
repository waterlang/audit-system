package com.lang.oliver.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * mysql变更后的数据信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 数据库名称
     */
    private String db;

    /**
     * 表名称
     */
    @Column(name = "db_table")
    private String dbTable;

    /**
     * 操作人ID
     */
    @Column(name = "operator_id")
    private Integer operatorId;

    /**
     * 操作类型insert/update/delete
     */
    @Column(name = "operation_type")
    private String operationType;

    /**
     * 操作前数据
     */
    @Column(name = "old_data")

    private String oldData;

    /**
     * 操作后数据
     */
    @Column(name = "new_data")

    private String newData;

    /**
     * 修改时间戳
     */
    @Column(name = "execution_time")

    private Long executionTime;

    /**
     * 执行的sql语句
     */
    @Column(name = "db_sql")
    private String dbSql;

    /**
     * 链路跟踪id
     */
    @Column(name = "trace_id")
    private String traceId;

    /**
     * mysql主键ID
     */
    @Column(name = "primary_ids")
    private String primaryIds;

    @Column(name = "create_time")
    private Date createTime;

}
