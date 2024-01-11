package com.frozenleafstudio.dev.automatedSetlist.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;



public class CustomUserDetailsService implements UserDetailsService{
        @Value("${ADMIN_USERNAME}")
        private String username;

        @Value("${ADMIN_PASSWORD}")
        private String encodedPassword;

        private static final String ROLE = "ADMIN";

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            if (!this.username.equals(username)) {
                throw new UsernameNotFoundException("User not found");
            }

            return new User(this.username, encodedPassword, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + ROLE)));
        }
    }
