package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    //Initialize spotify oauth2 authorization
    @GetMapping("/auth")
    public ResponseEntity<String> initiateAuthorization(){
        String url = authService.createAuthorizationURL();
        return ResponseEntity.ok("Please go to this URL to authorize: " + url);
    }
    //callback will automatically be called by spotify's API during authcode creation
    @GetMapping("/callback")
    public ResponseEntity<String> handleSpotifyCallback(@RequestParam String code, @RequestParam String state){
        if(!authService.validateState(state)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid State param");
        }
        tokenService.exchangeCode(code);
        return ResponseEntity.ok("Authorized Successfully");
    }
    //Fetch setlist from database using setlist ID, and search spotify tracks using the list of songs and the artist name(pulled from frontend because artistname isn't stored in setlist object)
    //finally, return prototype playlist.
    @GetMapping("/search")
    public ResponseEntity<Playlist> searchTracks(@RequestParam String setlistId, @RequestParam String artistName){
        Playlist tracksSearchResult = playlistService.searchAndProcessTracks(setlistId, artistName);
        if(tracksSearchResult.toString().isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(tracksSearchResult, HttpStatus.OK);
        }
    }
    //create a spotify playlist, fetch prototype playlist from DB, add spotify tracks to playlist and finally update the prototype to final playlist -> return playlist.
    @GetMapping("/create")
    public ResponseEntity<Playlist> createSpotifyPlaylist(
        @RequestParam String setlistId,
        @RequestParam(defaultValue = "false") boolean includeCovers) {
        Playlist tracksSearchResult = playlistService.createPlaylist(setlistId, includeCovers);
        if(tracksSearchResult.toString().isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(tracksSearchResult, HttpStatus.OK);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken() {
        boolean isRefreshed = tokenService.refreshSpotifyTokenPeriodically();
        if (isRefreshed) {
            return ResponseEntity.ok("Token refreshed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to refresh token");
        }
    }

}
