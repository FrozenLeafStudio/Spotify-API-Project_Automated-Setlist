package com.frozenleafstudio.dev.AutomatedSetlist.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpHeaders;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (log.isDebugEnabled()) {
            logRequest(request, body);
            ClientHttpResponse response = execution.execute(request, body);
            return logResponse(response);
        }
        return execution.execute(request, body);
    }

    private void logRequest(HttpRequest request, byte[] body) throws UnsupportedEncodingException {
        log.debug("URI: {}", request.getURI());
        log.debug("HTTP Method: {}", request.getMethod());
        log.debug("HTTP Headers: {}", request.getHeaders());
        log.debug("Request Body: {}", new String(body, StandardCharsets.UTF_8));
    }

    private ClientHttpResponse logResponse(ClientHttpResponse response) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream bodyStream = response.getBody()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bodyStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
        }

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
        String responseBody = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        log.debug("Response Body: {}", responseBody);
        log.debug("Response Status Code: {}", response.getStatusCode());
        log.debug("Response Headers: {}", response.getHeaders());

        return new ClientHttpResponse() {
            @Override
            public InputStream getBody() {
                return byteArrayInputStream;
            }

            @Override
            public HttpHeaders getHeaders() {
                return response.getHeaders();
            }

            @Override
            public HttpStatusCode getStatusCode() throws IOException {
                return response.getStatusCode();
            }

            @Override
            public String getStatusText() throws IOException {
                return response.getStatusText();
            }

            @Override
            public void close() {
                response.close();
            }
        };
    }
}
