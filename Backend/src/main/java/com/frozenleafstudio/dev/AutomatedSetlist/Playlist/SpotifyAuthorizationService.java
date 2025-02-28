package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpotifyAuthorizationService {
    private final SpotifyApi spotifyApi;
    private String storedState;

    @Autowired
    public SpotifyAuthorizationService(SpotifyApi spotifyApi){
        this.spotifyApi = spotifyApi;
    }
    public String createAuthorizationURL(){
        this.storedState = String.valueOf(RandomStringUtils.secure());
        AuthorizationCodeUriRequest authCodeUri = spotifyApi.authorizationCodeUri()
                                                .state(storedState)
                                                .scope("playlist-modify-public")
                                                .show_dialog(true)
                                                .build();

        URI uri = authCodeUri.execute();
        System.out.println("Authorization URI: " + uri);
        return uri.toString();

    }
    public boolean validateState(String receivedState){
        return storedState.equals(receivedState);
    }
}
