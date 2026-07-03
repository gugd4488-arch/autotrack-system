package com.autotrack.server.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 字符编码过滤器，确保所有请求和响应使用UTF-8编码
 */
@Component
public class CharacterEncodingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 设置请求编码
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        // 设置响应编码和内容类型
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        
        // 继续处理请求
        filterChain.doFilter(request, response);
    }
}
