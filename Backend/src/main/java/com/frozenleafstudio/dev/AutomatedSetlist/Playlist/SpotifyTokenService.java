package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

@Service
public class SpotifyTokenService {
    private final SpotifyApi spotifyApi;
    private final SpotifyTokenRepo tokenRepo;
    
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

    private void refreshToken() {
        try{
            AuthorizationCodeCredentials creds = spotifyApi.authorizationCodeRefresh().build().execute();
            LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(creds.getExpiresIn());
            saveToken(creds.getAccessToken(), creds.getRefreshToken(), expiryTime);
        } catch (Exception e){
            throw new RuntimeException("Failed to refresh Spotify Token : ", e);
        }
    }

    private boolean tokenIsExpired(SpotifyToken token) {
        return LocalDateTime.now().isAfter(token.getAccessTokenExpiry());
    }

    public SpotifyToken getSpotifyToken(){
        return tokenRepo.findAll().stream().findFirst().orElse(null);
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
