package com.frozenleafstudio.dev.automatedSetlist.playlist;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

@Service
public class SpotifyTokenService {
    private static final Logger log = LoggerFactory.getLogger(SpotifyTokenService.class);
    private final SpotifyApi spotifyApi;
    private final SpotifyTokenRepo tokenRepo;

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
            log.error("Error occurred while exchanging code: ", e);
            throw new RuntimeException("Failed to exchange Spotify code", e);
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
            throw new IllegalStateException("No Spotify token available. Please contact administrator");
        }else if(tokenIsExpired(token)){
            boolean isRefreshSuccessful = refreshTokenWithRetries(3);
            if (isRefreshSuccessful) {
                token = getSpotifyToken();
            } else {
                log.error("Failed to refresh the expired Spotify token. Continuing with the old token.");
            }
        }
        return token.getAccessToken();
    }

    public boolean refreshSpotifyTokenPeriodically(){
        SpotifyToken currentToken = getSpotifyToken();
        if(currentToken != null && tokenIsExpired(currentToken)){
            return refreshTokenWithRetries(3);
        }
        return false; 
    }

    private boolean refreshTokenWithRetries(int retryCount) {
        for (int i = 0; i < retryCount; i++) {
            try {
                if (refreshToken()) {
                    return true;
                }
            } catch (Exception e) {
                log.error("Attempt " + (i + 1) + " failed to refresh Spotify Token: " + e.getMessage(), e);
                if (i < retryCount - 1) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        log.error("All attempts to refresh Spotify Token failed.");
        return false;
    }

    private boolean refreshToken() {
        SpotifyToken existingToken = getSpotifyToken();
        if (existingToken == null || existingToken.getRefreshToken() == null || existingToken.getRefreshToken().isEmpty()) {
            log.error("Refresh token is null or empty. Cannot refresh Spotify token.");
            return false;
        }

        try {
            AuthorizationCodeCredentials creds = spotifyApi.authorizationCodeRefresh().build().execute();
            LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(creds.getExpiresIn());

            // Retain the existing refresh token if the API doesn't provide a new one
            String newRefreshToken = creds.getRefreshToken() != null ? creds.getRefreshToken() : existingToken.getRefreshToken();
            saveToken(creds.getAccessToken(), newRefreshToken, expiryTime);
            log.info("Spotify Token successfully refreshed.");
            return true;
        } catch (Exception e) {
            log.error("Failed to refresh Spotify Token: ", e);
            return false;
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
