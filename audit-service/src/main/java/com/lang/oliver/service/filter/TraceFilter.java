package com.lang.oliver.service.filter;


import com.lang.oliver.service.context.TraceContext;
import com.lang.oliver.service.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class TraceFilter implements Filter {

    public TraceFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String traceId = TraceIdUtil.getTraceIdFromRequest((HttpServletRequest)request);
        log.info("traceId :{}",traceId);

        TraceContext.setTraceId(traceId);

        chain.doFilter(request, response);
    }

    public void destroy() {
    }
}
