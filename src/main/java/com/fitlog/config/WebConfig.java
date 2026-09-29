package com.fitlog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Standard WebMvc configuration.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Interceptors disabled for simple MVP operation
}
