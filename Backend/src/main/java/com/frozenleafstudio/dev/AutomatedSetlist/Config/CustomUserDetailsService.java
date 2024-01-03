package com.frozenleafstudio.dev.AutomatedSetlist.Config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;


public class CustomUserDetailsService implements UserDetailsService{
    @Value("${ADMIN_USERNAME}")
    private String username;

    @Value("${ADMIN_PASSWORD}")
    private String rawPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ROLE = "ADMIN";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!this.username.equals(username)) {
            throw new UsernameNotFoundException("User not found");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);

        return new User(this.username, encodedPassword, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + ROLE)));
    }
}
