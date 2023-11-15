package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistController {
    private final SpotifyAuthorizationService authService;
    private final SpotifyTokenService tokenService;
    private final PlaylistService playlistService;

    public PlaylistController(SpotifyAuthorizationService authService, SpotifyTokenService tokenService, PlaylistService playlistService){
        this.authService = authService;
        this.tokenService = tokenService;
        this.playlistService = playlistService;
    }
    //
    @GetMapping("/auth")
    public ResponseEntity<String> initiateAuthorization(){
        String url = authService.createAuthorizationURL();
        return ResponseEntity.ok("Please go to this URL to authorize: " + url);
    }
    //callback will automatically be called by spotify's API during authcode creation - need to update spotify app settings to reflect final resting place of backend server
    @GetMapping("/callback")
    public ResponseEntity<String> handleSpotifyCallback(@RequestParam String code, @RequestParam String state){
        if(!authService.validateState(state)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid State param");
        }
        tokenService.exchangeCode(code);
        return ResponseEntity.ok("Authorized Successfully");
    }
    @GetMapping("/search")
    public ResponseEntity<List<Track>> searchTracks(@RequestParam String setlistId){
        List<Track> tracksSearchResult = playlistService.searchAndProcessTracks(setlistId);
        if(tracksSearchResult.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(tracksSearchResult, HttpStatus.OK);
        }
    }

}
