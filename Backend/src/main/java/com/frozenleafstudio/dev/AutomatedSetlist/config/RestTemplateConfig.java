package com.frozenleafstudio.dev.AutomatedSetlist.Config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
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

        connectionManager.setMaxTotal(10);
        connectionManager.setDefaultMaxPerRoute(5);

        Timeout socketTimeout = Timeout.ofMilliseconds(5000);
        SocketConfig socketConfig = SocketConfig.custom()
            .setSoTimeout(socketTimeout)
            .build();
        connectionManager.setDefaultSocketConfig(socketConfig);

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(10))
                .build())
            .evictIdleConnections(TimeValue.ofMinutes(1)) 
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
