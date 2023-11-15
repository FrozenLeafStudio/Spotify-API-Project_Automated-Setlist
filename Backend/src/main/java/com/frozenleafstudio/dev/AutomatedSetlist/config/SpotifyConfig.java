package com.frozenleafstudio.dev.AutomatedSetlist.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import se.michaelthelin.spotify.SpotifyApi;

@Configuration
public class SpotifyConfig {
    @Value("${spotify.clientId}")
    private String clientId;
    @Value("${spotify.clientSecret}")
    private String clientSecret;
    @Value("${redirectUri}")
    private String redirectUriString;

    @Bean
    public SpotifyApi spotifyApi(){
        try{
            URI uri = new URI(redirectUriString);
            return new SpotifyApi.Builder()
                            .setClientId(clientId)
                            .setClientSecret(clientSecret)
                            .setRedirectUri(uri).build();
        } catch(URISyntaxException e){
            throw new RuntimeException("Error initializing SpotifyApi :", e);
        }
    }
}
