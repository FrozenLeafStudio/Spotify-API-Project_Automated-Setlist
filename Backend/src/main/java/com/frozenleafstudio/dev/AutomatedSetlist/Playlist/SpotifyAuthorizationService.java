package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;

@Service
public class SpotifyAuthorizationService {
    private final SpotifyApi spotifyApi;
    private String storedState;

    @Autowired
    public SpotifyAuthorizationService(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public String createAuthorizationURL() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z')
                .build();

        this.storedState = generator.generate(32);

        AuthorizationCodeUriRequest authCodeUri = spotifyApi.authorizationCodeUri()
                .state(storedState)
                .scope("playlist-modify-public")
                .show_dialog(true)
                .build();

        URI uri = authCodeUri.execute();
        System.out.println("Authorization URI: " + uri);
        return uri.toString();
    }

    public boolean validateState(String receivedState) {
        return storedState != null && storedState.equals(receivedState);
    }
}
