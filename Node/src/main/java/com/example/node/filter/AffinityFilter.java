package com.example.node.filter;

import com.example.node.model.query.OperationType;
import com.example.node.model.request.QueryRequest;
import com.example.node.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@Order(2)
public class AffinityFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        RequestWrapper wrapper = new RequestWrapper((HttpServletRequest) servletRequest);

        if (wrapper.getRequestURI().contains("query/processQuery")) {

            byte[] body = StreamUtils.copyToByteArray(wrapper.getInputStream());
            QueryRequest queryRequest = JSONUtil.parseObject(body, QueryRequest.class);

            if (OperationType.isBroadCastable(queryRequest.getOperation())) {
                RequestDispatcher dispatcher = wrapper.getRequestDispatcher("/affinity/processAffinity");
                log.info("dispatcher.forward " + queryRequest);
                dispatcher.forward(wrapper, response);
            } else {
                filterChain.doFilter(wrapper, servletResponse);
            }
        } else {
            filterChain.doFilter(wrapper, servletResponse);
        }
    }
}
