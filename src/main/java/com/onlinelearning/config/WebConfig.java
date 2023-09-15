package com.onlinelearning.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // for all endpoints in the application
               .allowedOrigins("*") // allowed origins, can be specific domains
               .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // allowed methods
               .allowedHeaders("*") // allowed headers
               .allowCredentials(false) // if you allow cookies, e.g. for session authentication
               .maxAge(3600); // max age for preflight requests (OPTION requests)
    }
}
