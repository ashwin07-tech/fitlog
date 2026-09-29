package com.fitlog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger Documentation Configuration for FitLog.
 * Grounded in Sri Eshwar College of Engineering Assessment Requirements.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fitLogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FitLog — Personal Workout and Calorie Tracker API")
                        .description("Sri Eshwar College of Engineering · Project Leap (Regulation 2023)\n\n" +
                                "Comprehensive REST API backend for tracking workouts, meals, daily calorie balance, " +
                                "and weekly goal metrics. Fully implements all business rules, service-layer validation, " +
                                "and global exception handling.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FitLog Engineering Team")
                                .email("student@sece.ac.in"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://spring.io/projects/spring-boot")));
    }
}
