package com.frozenleafstudio.dev.AutomatedSetlist.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
import java.time.Duration;


@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .errorHandler(new DefaultResponseErrorHandler() {
                @Override
                public void handleError(ClientHttpResponse response) throws IOException {
                    // Your custom error handling logic here
                    try {
                        super.handleError(response);
                    } catch (HttpClientErrorException | HttpServerErrorException e) {
                        // Maybe rethrow a custom exception or handle differently
                        throw e;
                    }
                }
            })
            .setConnectTimeout(Duration.ofSeconds(10)) // Set connection timeout
            .setReadTimeout(Duration.ofSeconds(10))    // Set read timeout
            .build();
    }
}