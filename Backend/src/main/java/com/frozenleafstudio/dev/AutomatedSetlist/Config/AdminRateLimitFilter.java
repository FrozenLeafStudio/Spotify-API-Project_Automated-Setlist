package com.frozenleafstudio.dev.AutomatedSetlist.Config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Throttles the admin endpoints, ahead of authentication.
public class AdminRateLimitFilter extends OncePerRequestFilter {

    private final RateLimiter limiter = RateLimiter.of("admin", RateLimiterConfig.custom()
            .limitForPeriod(5)
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .timeoutDuration(Duration.ZERO)
            .build());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (isAdminPath(request.getRequestURI()) && !limiter.acquirePermission()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isAdminPath(String uri) {
        for (String path : SecurityConfig.ADMIN_ENDPOINTS) {
            if (uri.startsWith(path)) {
                return true;
            }
        }
        return false;
    }
}
