package com.autotrack.server.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.nio.charset.StandardCharsets;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, 
            org.springframework.http.HttpStatus status, WebRequest request) {
        
        // 强制设置UTF-8字符集
        if (headers.getContentType() == null) {
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        } else {
            MediaType currentType = headers.getContentType();
            headers.setContentType(new MediaType(
                currentType.getType(),
                currentType.getSubtype(),
                StandardCharsets.UTF_8
            ));
        }
        
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}
