package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

@Service
public class SpotifyTokenService {
    private final SpotifyApi spotifyApi;
    private final SpotifyTokenRepo tokenRepo;
    private static final Logger log = LoggerFactory.getLogger(SpotifyTokenService.class);

    
    @Autowired
    public SpotifyTokenService(SpotifyTokenRepo tokenRepo, SpotifyApi spotifyApi){
        this.tokenRepo = tokenRepo;
        this.spotifyApi = spotifyApi;
    }
    
    public void exchangeCode(String code){
        try{
            AuthorizationCodeCredentials creds = spotifyApi.authorizationCode(code).build().execute();
            LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(creds.getExpiresIn());
            saveToken(creds.getAccessToken(), creds.getRefreshToken(), expiryTime);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    private boolean tokenIsExpired(SpotifyToken token) {
        return LocalDateTime.now().isAfter(token.getAccessTokenExpiry());
    }

    public SpotifyToken getSpotifyToken(){
        return tokenRepo.findAll().stream().findFirst().orElse(null);
    }
    public String getCurrentAccessToken(){
        SpotifyToken token = getSpotifyToken();
        if(token == null){
            //since first initialization will occur using my service account, this will return an exception w/ messaging
            throw new IllegalStateException("No Spotify token available. Please contact administrator");
        }else if(tokenIsExpired(token)){
            //init refresh workflow
            refreshToken();
            token = getSpotifyToken();
        }
        return token.getAccessToken();
    }
    @Scheduled(fixedRate = 1000000)
    public void refreshSpotifyTokenPeriodically(){
        SpotifyToken currentToken = getSpotifyToken();
        if(currentToken != null && tokenIsExpired(currentToken)){
            refreshTokenWithRetries(3); // Example: Retry up to 3 times
        }
    }

    private void refreshTokenWithRetries(int retryCount) {
        for (int i = 0; i < retryCount; i++) {
            try {
                refreshToken();
                return; // Break out of the loop on success
            } catch (Exception e) {
                log.error("Attempt " + (i + 1) + " failed to refresh Spotify Token: " + e.getMessage(), e);
                if (i < retryCount - 1) {
                    // Wait for some time before retrying
                    try {
                        Thread.sleep(1000); // 1 second
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        log.error("All attempts to refresh Spotify Token failed.");
    }

    private void refreshToken() {
        SpotifyToken token = getSpotifyToken();
        if (token == null || token.getRefreshToken() == null || token.getRefreshToken().isEmpty()) {
            log.error("Refresh token is null or empty. Cannot refresh Spotify token.");
            throw new IllegalStateException("Refresh token is not available");
        }

        try {
            AuthorizationCodeCredentials creds = spotifyApi.authorizationCodeRefresh().build().execute();
            LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(creds.getExpiresIn());
            saveToken(creds.getAccessToken(), creds.getRefreshToken(), expiryTime);
            log.info("Spotify Token successfully refreshed.");
        } catch (Exception e) {
            log.error("Failed to refresh Spotify Token: ", e);
            throw new RuntimeException("Failed to refresh Spotify Token: ", e);
        }
    }


    public void saveToken(String accessToken, String refreshToken, LocalDateTime expiry){
        SpotifyToken token = getSpotifyToken();
        if(token == null){
            token = new SpotifyToken();
        }
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setAccessTokenExpiry(expiry);
        tokenRepo.save(token);
        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);
    }
}
