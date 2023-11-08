package com.frozenleafstudio.dev.AutomatedSetlist.config;

//Class created because of CORS error during development - ReactJS localhost couldn't interact with Spring localhost

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /* ChatGPT suggestion for production CORS mapping
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**") // This applies to all API endpoints
                    .allowedOrigins("https://frozenleafstudio.com") // Replace with your actual frontend domain
                    // If you have multiple domains, you can add them like this:
                    // .allowedOrigins("https://frozenleafstudio.com", "https://anotherdomain.com")
                    .allowedMethods("GET", "POST") // Only allowing GET and POST methods
                    .allowedHeaders("*") // You might want to restrict this to certain headers
                    .allowCredentials(true); // If you need to include credentials such as cookies, authorization headers, etc.
        } */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // This allows all endpoints in your API
                .allowedOrigins("*") // This allows access from all origins
                .allowedMethods("GET", "POST") // This allows only GET and POST methods
                .allowedHeaders("*"); // This allows all headers
    }
}