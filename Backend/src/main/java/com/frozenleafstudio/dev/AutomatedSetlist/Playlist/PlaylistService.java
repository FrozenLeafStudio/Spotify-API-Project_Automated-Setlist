package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.frozenleafstudio.dev.AutomatedSetlist.Setlist.Setlist;
import com.frozenleafstudio.dev.AutomatedSetlist.Setlist.SetlistService;
import org.apache.hc.core5.http.ParseException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
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
    private Playlist fetchPlaylist(String setlistId){
        return playlistRepository.getBysetlistID(setlistId);
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
            Setlist pullSetlist = setlistRepository.getSetlistById(setlistId);
            String setlistName = artistName + " @" + pullSetlist.getVenueName() + " in " + pullSetlist.getVenueLocation();
            List<String> setlistTracks = pullSetlist.getSongs();
            List<AppTrack> tracks = searchTracks(setlistTracks, artistName);

            playlist.setName(setlistName);
            playlist.setTracks(tracks);
            playlist.setSetlistID(setlistId);
            playlist.setDescription("Playlist created using data from Setlist.FM. The tracks were pull from the following Setlist.FM URL: " + pullSetlist.getUrl());
            playlistRepository.save(playlist);
        }
        return playlistRepository.getBysetlistID(setlistId);
    }
    public Playlist createPlaylist(String setlistId) {
        Playlist playlist = fetchPlaylist(setlistId);
        if(playlist == null){
            return null;
        }
        return SpotifyPlaylist(playlist);
    }
    private Playlist SpotifyPlaylist(Playlist prototypePlaylist) {
        List<String> songs = new ArrayList<>();
        try{
            CreatePlaylistRequest createPlaylist = spotifyApi.createPlaylist("31fht62ert5mwjiajazfyuqf2dhm",prototypePlaylist.getName())
                                                    .public_(true)
                                                    .description(prototypePlaylist.getDescription())
                                                    .build();
            se.michaelthelin.spotify.model_objects.specification.Playlist completePlaylist = createPlaylist.execute();
            for(AppTrack song : prototypePlaylist.getTracks()){
                songs.add(song.getSongUri());
            }
            String[] songsArray = songs.toArray(new String[0]);
            AddItemsToPlaylistRequest addTracksToPlaylist = spotifyApi.addItemsToPlaylist(completePlaylist.getId(), songsArray).build();
            SnapshotResult results = addTracksToPlaylist.execute();
            prototypePlaylist.setPlaylist_id(completePlaylist.getId());
            prototypePlaylist.setSpotifyUrl(completePlaylist.getExternalUrls().get("spotify"));
            playlistRepository.save(prototypePlaylist);
            System.err.println(completePlaylist + " " + results);

        } catch(IOException | SpotifyWebApiException | ParseException e){
                System.out.println("Error: " + e.getMessage());
        }
        return prototypePlaylist;
    }
    
}
