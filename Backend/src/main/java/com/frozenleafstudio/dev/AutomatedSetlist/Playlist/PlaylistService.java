package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;

@Service
public class PlaylistService {
    private final SpotifyApi spotifyApi;
    private final SpotifyTokenService spotifyTokenService;

    @Autowired
    public PlaylistService(SpotifyApi spotifyApi, SpotifyTokenService spotifyTokenService){
        this.spotifyApi = spotifyApi;
        this.spotifyTokenService = spotifyTokenService;
    }

    public List<Track> searchAndProcessTracks(String setlistId) {
        return null;
    }
    
}
