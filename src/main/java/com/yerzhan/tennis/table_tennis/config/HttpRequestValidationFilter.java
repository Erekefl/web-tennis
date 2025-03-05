package com.yerzhan.tennis.table_tennis.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpRequestValidationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestValidationFilter.class);
    private static final Pattern VALID_METHODS = Pattern.compile("^(GET|POST|PUT|DELETE|HEAD|OPTIONS|TRACE|PATCH)$");
    private static final Pattern VALID_PATH = Pattern.compile("^[a-zA-Z0-9/._-]*$");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String method = httpRequest.getMethod();
        String path = httpRequest.getRequestURI();

        // Проверяем метод HTTP-запроса
        if (!VALID_METHODS.matcher(method).matches()) {
            logger.warn("Обнаружен недопустимый HTTP-метод: {}", method);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid HTTP method");
            return;
        }

        // Проверяем путь запроса
        if (!VALID_PATH.matcher(path).matches()) {
            logger.warn("Обнаружен недопустимый путь запроса: {}", path);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }

        chain.doFilter(request, response);
    }
} 