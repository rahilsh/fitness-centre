package com.rsh.fitness_centre.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * Filter for logging HTTP requests and responses.
 * Logs method, path, status code, and body content for debugging.
 */
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 10000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logRequest(requestWrapper);
            logResponse(responseWrapper, duration);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String queryString = request.getQueryString();
        String uri = queryString != null ? request.getRequestURI() + "?" + queryString : request.getRequestURI();

        logger.debug("HTTP Request: {} {} | Headers: {}",
                request.getMethod(),
                uri,
                getHeaders(request));

        if ("POST".equalsIgnoreCase(request.getMethod()) ||
                "PUT".equalsIgnoreCase(request.getMethod()) ||
                "PATCH".equalsIgnoreCase(request.getMethod())) {
            String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
            if (!body.isEmpty()) {
                if (body.length() > MAX_PAYLOAD_LENGTH) {
                    logger.debug("Request Body: {} (truncated, total length: {})",
                            body.substring(0, MAX_PAYLOAD_LENGTH),
                            body.length());
                } else {
                    logger.debug("Request Body: {}", body);
                }
            }
        }
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        logger.debug("HTTP Response: Status {} | Duration: {}ms | Content-Type: {}",
                response.getStatus(),
                duration,
                response.getContentType());

        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, StandardCharsets.UTF_8);
            if (body.length() > MAX_PAYLOAD_LENGTH) {
                logger.debug("Response Body: {} (truncated, total length: {})",
                        body.substring(0, MAX_PAYLOAD_LENGTH),
                        body.length());
            } else {
                logger.debug("Response Body: {}", body);
            }
        }
    }

    private String getHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            // Mask sensitive headers
            if (isSensitiveHeader(headerName)) {
                headers.append(headerName).append("=***MASKED*** ");
            } else {
                headers.append(headerName).append("=").append(headerValue).append(" ");
            }
        }
        return headers.toString();
    }

    private boolean isSensitiveHeader(String headerName) {
        String lowerName = headerName.toLowerCase();
        return lowerName.contains("authorization") ||
               lowerName.contains("password") ||
               lowerName.contains("token") ||
               lowerName.contains("api-key") ||
               lowerName.contains("secret");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip logging for static resources and health checks
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/webjars") ||
               path.equals("/health");
    }
}
