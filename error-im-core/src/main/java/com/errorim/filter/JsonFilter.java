package com.errorim.filter;

import com.errorim.wrapper.RequestWrapper;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author ErrorRua
 * @date 2022/11/18
 * @description:
 */

@Component
public class JsonFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //请求参数为JSON类型，则使用自定义包装
        if(request instanceof HttpServletRequest
                && "application/json".equals(((HttpServletRequest)request).getHeader("Content-Type"))) {
            chain.doFilter(new RequestWrapper((HttpServletRequest)request), response);
        } else {
            chain.doFilter(request, response);
        }
    }
    public void destroy() {
    }
}
