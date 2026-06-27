package com.rsh.fitness_centre.config;

import com.rsh.fitness_centre.filter.RequestResponseLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for logging and monitoring.
 * Registers request/response logging filter and configures SLF4J/Logback.
 */
@Configuration
public class LoggingConfig {

    /**
     * Registers the request/response logging filter.
     * Logs HTTP method, path, status code, and body content.
     *
     * @return RequestResponseLoggingFilter bean
     */
    @Bean
    public RequestResponseLoggingFilter requestResponseLoggingFilter() {
        return new RequestResponseLoggingFilter();
    }
}
