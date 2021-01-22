package com.lang.oliver.service.filter;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lang.oliver.service.context.LoginContext;
import com.lang.oliver.service.context.TraceContext;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
public class AuditCDCLogInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(AuditCDCLogInterceptor.class);
    private boolean enable = true;
    private List<String> commandTypes;

    public AuditCDCLogInterceptor() {
        this.commandTypes = Arrays.asList(SqlCommandType.INSERT.toString(), SqlCommandType.UPDATE.toString(), SqlCommandType.DELETE.toString());
        String interceptorEnabled = System.getProperty("auditlog.interceptor.enable");
        if (interceptorEnabled != null && !Boolean.valueOf(interceptorEnabled)) {
            this.enable = false;
        }

    }

    public Object intercept(Invocation invocation) throws Throwable {
        if (!this.enable) {
            return invocation.proceed();
        } else if (!this.isAuditCommandType(invocation)) {
            return invocation.proceed();
        } else {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = statementHandler.getBoundSql();
            this.modifySql(boundSql);
            return invocation.proceed();
        }

    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
        String auditTables = (String) properties.get("auditTables");
        if (StringUtils.isBlank(auditTables)) {
            throw new IllegalArgumentException("Property auditTables is required");
        } else {
            String commandTypes = (String) properties.get("commandTypes");
            if (StringUtils.isNotBlank(commandTypes)) {
                this.commandTypes = Arrays.asList(commandTypes.toUpperCase().split(","));
            }
        }
    }


    private boolean isAuditCommandType(Invocation invocation) {
        try {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            return sqlCommandType == null ? false : this.commandTypes.contains(sqlCommandType.toString());
        } catch (Exception var6) {
            logger.error("Failed check sql command type", var6);
            return false;
        }
    }

    private void modifySql(BoundSql boundSql) {
        try {
            Long customerId = LoginContext.getLoginUserId();
            String operatorId = customerId != null ? String.valueOf(customerId) : "unknown";
            String updatedSql = String.format("/*@%s,%s@*/ %s", operatorId, TraceContext.getTraceId(), boundSql.getSql());
            logger.debug("updatedSql: {}", updatedSql);
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, updatedSql);
        } catch (Exception var6) {
            logger.error("Failed modify sql", var6);
        }

    }


}
