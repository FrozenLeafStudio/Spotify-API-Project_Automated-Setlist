package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistController {
     @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;
    @GetMapping
    public ResponseEntity<String> allPlaylists(){
        return new ResponseEntity<String>("This will show all searched playlists & testing env variables " + "clientID: " + clientId + " Secret: " + clientSecret, HttpStatus.OK);

    }

    // Methods for Spotify API interactions, including searching and playlist creation
}
