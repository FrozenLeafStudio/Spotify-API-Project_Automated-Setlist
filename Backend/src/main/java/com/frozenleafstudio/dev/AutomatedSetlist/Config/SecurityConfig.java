package com.frozenleafstudio.dev.AutomatedSetlist.Config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Admin-only endpoints. /callback is excluded; it validates the OAuth state parameter instead.
    public static final String[] ADMIN_ENDPOINTS = {
        "/api/v1/playlists/auth",
        "/api/v1/playlists/refresh-token",
        "/api/v1/setlists/db/purge",
        "/api/v1/artists/populateDB/admin"
    };

     @Autowired
    private Environment env;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disabling CSRF
            .authorizeHttpRequests(authz -> {
                if ("local".equals(env.getProperty("app.environment"))) {
                    authz.anyRequest().permitAll(); // All requests are allowed in local environment
                } else {
                    authz
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                        .anyRequest().permitAll();
                }
            })
            .addFilterBefore(new AdminRateLimitFilter(), BasicAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults());

        configureCors(http);

        return http.build();
    }

    private void configureCors(HttpSecurity http) throws Exception {
        http.cors(cors -> {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();

            if ("local".equals(env.getProperty("app.environment"))) {
                // For local testing
                config.setAllowedOrigins(Arrays.asList("*")); 
            } else {
                // Production origins
                config.setAllowedOrigins(Arrays.asList("https://vite.frozenleafstudio.com"));
            }

            config.setAllowedMethods(Arrays.asList("GET", "POST"));
            config.setAllowedHeaders(Arrays.asList("Content-Type"));
            source.registerCorsConfiguration("/**", config);
            cors.configurationSource(source);
        });
    }

    @Bean
    public UserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean 
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}