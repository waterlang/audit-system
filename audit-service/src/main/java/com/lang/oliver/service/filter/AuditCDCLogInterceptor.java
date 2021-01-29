package com.lang.oliver.service.filter;

import com.lang.oliver.service.context.LoginContext;
import com.lang.oliver.service.context.TraceContext;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;

@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
public class AuditCDCLogInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(AuditCDCLogInterceptor.class);


    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        this.modifySql(boundSql);
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
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
