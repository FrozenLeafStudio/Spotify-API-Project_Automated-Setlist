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
    //fetching current token will cause the expiry to be checked and refreshed if expired. 
    private boolean fetchToken(){
        String checkToken = this.spotifyTokenService.getCurrentAccessToken();
        if(!checkToken.isEmpty()){
            return true;
        }else{
            return false;
        }
        
    }
    private List<AppTrack> searchTracks(List<String> setlistSongs, String artistName){
        List<AppTrack> searchedTracks = new ArrayList<>();
        String name = " artist:"+artistName;
        for(String song : setlistSongs){
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(song+name).build();
            try{
                Paging<Track> trackPaging = searchTracksRequest.execute();
                if(trackPaging.getTotal()>0){
                    Track spotifyTrack = trackPaging.getItems()[0];
                    AppTrack appTrack = convertToTrackModel(spotifyTrack, true);
                    searchedTracks.add(appTrack);
                }
                if(trackPaging.getTotal() == 0){
                    AppTrack missingAppTrack = new AppTrack(false, null, song, artistName, null, null);
                    searchedTracks.add(missingAppTrack);
                }
                System.out.println("Total: " + trackPaging.getTotal());
            } catch(IOException | SpotifyWebApiException | ParseException e){
                System.out.println("Error: " + e.getMessage());
            }
        }
        return searchedTracks;

    }
    private AppTrack convertToTrackModel(Track spotifyTrack, boolean trackStatus) {
        String albumImageUrl = spotifyTrack.getAlbum().getImages().length > 0 ? spotifyTrack.getAlbum().getImages()[0].getUrl() : null;
        boolean trackfound = trackStatus;
        return new AppTrack(
            trackfound,
            spotifyTrack.getUri(),
            spotifyTrack.getName(), 
            spotifyTrack.getArtists()[0].getName(), 
            spotifyTrack.getAlbum().getName(), 
            albumImageUrl);
    }
    public Playlist searchAndProcessTracks(String setlistId, String artistName) {
        if(fetchToken()==true){
            Playlist playlist = new Playlist();
            if(playlistRepository.getBysetlistID(setlistId) == null){
                Setlist pullSetlist = setlistRepository.getSetlistById(setlistId);
                List<String> setlistTracks = pullSetlist.getSongs();
                List<AppTrack> tracks = searchTracks(setlistTracks, artistName);
                String setlistName = tracks.get(0).getArtistName() + " @ " + pullSetlist.getVenueName() + " in " + pullSetlist.getVenueLocation() + " " + pullSetlist.getEventDate();


                playlist.setName(setlistName);
                playlist.setTracks(tracks);
                playlist.setSetlistID(setlistId);
                playlist.setDescription("Playlist created using data from Setlist.FM. The tracks were pulled from the following Setlist.FM URL: " + pullSetlist.getUrl());
                playlistRepository.save(playlist);
            }
            return playlistRepository.getBysetlistID(setlistId);
        }
        return null;
    }
    public Playlist createPlaylist(String setlistId) {
        if(fetchToken()==true){
            Playlist playlist = fetchPlaylist(setlistId);
            if(playlist == null){
                return null;
            }
            return SpotifyPlaylist(playlist);
        }
        return null;
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
                if(song.isTrackFound())
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
