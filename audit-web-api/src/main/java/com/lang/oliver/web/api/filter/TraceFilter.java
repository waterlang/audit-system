package com.lang.oliver.web.api.filter;

import com.lang.oliver.web.api.trace.TraceContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class TraceFilter implements Filter {

    public TraceFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        TraceContext.setTraceId();
        chain.doFilter(request, response);
    }

    public void destroy() {
    }
}
