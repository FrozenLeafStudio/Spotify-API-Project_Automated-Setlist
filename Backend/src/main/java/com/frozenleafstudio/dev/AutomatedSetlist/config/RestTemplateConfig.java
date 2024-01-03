package com.frozenleafstudio.dev.AutomatedSetlist.Config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;


@Configuration
public class RestTemplateConfig {


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .additionalInterceptors(createRequestResponseLoggingInterceptor())
            .build();
    }

    private ClientHttpRequestInterceptor createRequestResponseLoggingInterceptor() {
        return new RequestResponseLoggingInterceptor();
    }
}