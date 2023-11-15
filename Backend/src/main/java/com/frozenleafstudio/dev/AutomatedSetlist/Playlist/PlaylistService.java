package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.frozenleafstudio.dev.AutomatedSetlist.Setlist.SetlistService;
import org.apache.hc.core5.http.ParseException;
import org.bson.types.ObjectId;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

@Service
public class PlaylistService {
    private final SpotifyApi spotifyApi;
    private final SpotifyTokenService spotifyTokenService;
    private SetlistService setlistRepository;
    private PlaylistRepo playlistRepository;

    @Autowired
    public PlaylistService(SpotifyApi spotifyApi, SpotifyTokenService spotifyTokenService, SetlistService setlistRepository, PlaylistRepo playlistRepository){
        this.spotifyApi = spotifyApi;
        this.spotifyTokenService = spotifyTokenService;
        this.setlistRepository = setlistRepository;
        this.playlistRepository = playlistRepository;
    }
    private List<AppTrack> searchTracks(List<String> setlistSongs, String artistName){
        List<AppTrack> foundTracks = new ArrayList<>();
        String name = " artist:"+artistName;
        for(String song : setlistSongs){
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(song+name).build();
            try{
                Paging<Track> trackPaging = searchTracksRequest.execute();
                if(trackPaging.getTotal()>0){
                    Track spotifyTrack = trackPaging.getItems()[0];
                    AppTrack appTrack = convertToTrackModel(spotifyTrack);
                    foundTracks.add(appTrack);
                }
                System.out.println("Total: " + trackPaging.getTotal());
            } catch(IOException | SpotifyWebApiException | ParseException e){
                System.out.println("Error: " + e.getMessage());
            }
        }
        return foundTracks;

    }
    private AppTrack convertToTrackModel(Track spotifyTrack) {
        String albumImageUrl = spotifyTrack.getAlbum().getImages().length > 0 ? spotifyTrack.getAlbum().getImages()[0].getUrl() : null;
        return new AppTrack(
            spotifyTrack.getUri(),
            spotifyTrack.getName(), 
            spotifyTrack.getArtists()[0].getName(), 
            spotifyTrack.getAlbum().getName(), 
            albumImageUrl);
    }
    public Playlist searchAndProcessTracks(String setlistId, String artistName) {
        Playlist playlist = new Playlist();
        if(playlistRepository.getBysetlistID(setlistId) == null){
            List<String> pullSetlist = setlistRepository.getSetlistById(setlistId).getSongs();
            List<AppTrack> tracks = searchTracks(pullSetlist, artistName);

            playlist.setName("Prototype Playlist for " + artistName);
            playlist.setTracks(tracks);
            playlist.setSetlistID(setlistId);
            playlistRepository.save(playlist);
        }
        return playlistRepository.getBysetlistID(setlistId);
    }
    
}
