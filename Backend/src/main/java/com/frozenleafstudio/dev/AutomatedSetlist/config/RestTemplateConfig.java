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
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(25);

        Timeout socketTimeout = Timeout.ofSeconds(10);
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(socketTimeout)
                .build();
        connectionManager.setDefaultSocketConfig(socketConfig);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(Timeout.ofSeconds(10))
                        .setResponseTimeout(Timeout.ofSeconds(20))
                        .build())
                .evictIdleConnections(TimeValue.ofMinutes(1))
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return restTemplateBuilder
                .requestFactory(() -> requestFactory)
                .additionalInterceptors(createRetryInterceptor())
                .build();
    }

    private ClientHttpRequestInterceptor createRetryInterceptor() {
        return (request, body, execution) -> {
            int maxRetries = 3;
            int attempt = 0;

            while (attempt < maxRetries) {
                try {
                    return execution.execute(request, body);
                } catch (IOException e) {
                    if (attempt == maxRetries - 1 || !shouldRetry(e)) {
                        throw e;
                    }
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Retry interrupted", ie);
                    }
                    attempt++;
                }
            }
            throw new IOException("Request failed after " + maxRetries + " attempts");
        };
    }

    private boolean shouldRetry(IOException e) {
        return e instanceof SocketTimeoutException || e instanceof UnknownHostException;
    }
}
