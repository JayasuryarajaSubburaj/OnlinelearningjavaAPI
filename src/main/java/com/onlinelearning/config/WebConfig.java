package com.onlinelearning.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	 @Override
	    public void addCorsMappings(CorsRegistry registry) {
	        registry.addMapping("/**") // for all endpoints in the application
	               .allowedOrigins("http://localhost:3000") // replace with your frontend domain
	               .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // allowed methods
	               .allowedHeaders("*") // allowed headers
	               .exposedHeaders("Set-Cookie") // expose "Set-Cookie" header
	               .allowCredentials(true) // allow cookies
	               .maxAge(3600); // max age for preflight requests (OPTION requests)
	    }
}
