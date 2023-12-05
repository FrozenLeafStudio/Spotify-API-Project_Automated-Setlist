package com.frozenleafstudio.dev.AutomatedSetlist.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(10)) // Set connection timeout
            .setReadTimeout(Duration.ofSeconds(10))    // Set read timeout
            .additionalInterceptors(createRequestResponseLoggingInterceptor()) // Add logging interceptor
            .build();
    }

    private ClientHttpRequestInterceptor createRequestResponseLoggingInterceptor() {
        // Implement the interceptor to log request and response details
        return (request, body, execution) -> {
            // Log the request details
            System.out.println("Request to URI: " + request.getURI());
            System.out.println("Request Method: " + request.getMethod());
            System.out.println("Request Headers: " + request.getHeaders());

            // Execute the request
            var response = execution.execute(request, body);

            // Log the response details
            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Headers: " + response.getHeaders());

            return response;
        };
    }
}