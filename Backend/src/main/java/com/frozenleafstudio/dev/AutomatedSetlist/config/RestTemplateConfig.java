package com.frozenleafstudio.dev.automatedSetlist.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        // Setting socket timeout
        Timeout socketTimeout = Timeout.ofMilliseconds(5000);
        SocketConfig socketConfig = SocketConfig.custom()
            .setSoTimeout(socketTimeout)
            .build();
        connectionManager.setDefaultSocketConfig(socketConfig);

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(10)) // Connection request timeout
                .setResponseTimeout(Timeout.ofSeconds(10))          // Response timeout
                .build())
            .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return restTemplateBuilder
            .requestFactory(() -> requestFactory)
            .additionalInterceptors(createRequestResponseLoggingInterceptor())
            .build();
    }

    private ClientHttpRequestInterceptor createRequestResponseLoggingInterceptor() {
        return new RequestResponseLoggingInterceptor();
    }
}
