package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs.SetDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs.SetsDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs.SongDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs.VenueDTO;
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
    private static final Logger log = LoggerFactory.getLogger(PlaylistService.class);

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
    private List<AppTrack> searchTracks(SetsDTO setsDTO, String artistName) {
        log.info("Initiating track search for artist: {}", artistName);
        List<CompletableFuture<AppTrack>> futures = new ArrayList<>();
        if (setsDTO != null) {
            for (SetDTO set : setsDTO.getSet()) {
                for (SongDTO song : set.getSong()) {
                    boolean isTape = song.getTape() != null && song.getTape();
                    boolean isCover = song.getCover() != null;

                    if (isTape) {
                        futures.add(CompletableFuture.completedFuture(new AppTrack(false, null, song.getName(), artistName, null, null, 
                                                        createDetailsString(song, artistName, isCover), true, false)));
                        log.info("Added tape track: {} by artist: {}", song.getName(), artistName);
                    } else {
                        futures.add(searchSpotifyForTrack(song, artistName, isCover));
                        log.info("Initiating Spotify search for track: {} by artist: {}", song.getName(), artistName);
                    }
                }
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return futures.stream()
                      .map(CompletableFuture::join)
                      .collect(Collectors.toList());
    }

    @Async
    private CompletableFuture<AppTrack> searchSpotifyForTrack(SongDTO song, String artistName, boolean isCover) {
        log.info("Starting async Spotify search for track: {} by artist: {}", song.getName(), artistName);
        AppTrack appTrack = searchSpotifyAndCreateAppTrack(song.getName(), artistName, song, isCover);

        if (isCover) {
            boolean originalArtistMatch = appTrack.isTrackFound() && appTrack.getArtistName().equalsIgnoreCase(artistName);
            if (!appTrack.isTrackFound() || !originalArtistMatch) {
                String coverArtistName = song.getCover().getName();
                log.info("Cover song found. Searching for cover version: {} by {}", song.getName(), coverArtistName);
                appTrack = searchSpotifyAndCreateAppTrack(song.getName(), coverArtistName, song, true);
            } else {
                appTrack.setCover(false);
            }
        }

        return CompletableFuture.completedFuture(appTrack);
    }

    private AppTrack searchSpotifyAndCreateAppTrack(String trackName, String artistName, SongDTO song, boolean isCover) {
        String query = trackName + " artist:" + artistName;
        log.info("Executing Spotify track search for query: {}", query);
        SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(query).build();

        try {
            Paging<Track> trackPaging = searchTracksRequest.execute();
            if (trackPaging.getTotal() > 0) {
                Track spotifyTrack = trackPaging.getItems()[0];
                return convertSpotifyTrackToAppTrack(spotifyTrack, song, true, isCover, artistName);
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error during Spotify track search: {}", e.getMessage());
        }

        return new AppTrack(false, null, song.getName(), artistName, null, null, 
                            createDetailsString(song, artistName, isCover), false, isCover);
    }

    private AppTrack convertSpotifyTrackToAppTrack(Track spotifyTrack, SongDTO songDTO, boolean trackStatus, boolean isCover, String artistName) {
        log.info("Converting Spotify track to AppTrack: {}", spotifyTrack.getName());
        String albumImageUrl = spotifyTrack.getAlbum().getImages().length > 0 ? spotifyTrack.getAlbum().getImages()[0].getUrl() : null;

        return new AppTrack(
            trackStatus,
            spotifyTrack.getUri(),
            spotifyTrack.getName(), 
            spotifyTrack.getArtists()[0].getName(), 
            spotifyTrack.getAlbum().getName(), 
            albumImageUrl,
            createDetailsString(songDTO, artistName, isCover),
            songDTO.getTape() != null && songDTO.getTape(),
            isCover
        );
    }

    private String createDetailsString(SongDTO songDTO, String artistName, boolean isCover) {
        log.info("Creating details string for song: {}", songDTO.getName());
        StringBuilder details = new StringBuilder();
        if (songDTO.getInfo() != null) {
            details.append(songDTO.getInfo());
        }
        if (isCover) {
            if (details.length() > 0) details.append("; ");
            details.append(artistName)
                .append(" covering ")
                .append(songDTO.getCover().getName())
                .append("'s song: ")
                .append(songDTO.getName());
        }
        if (songDTO.getWith() != null) {
            if (details.length() > 0) details.append("; ");
            details.append("Featuring: ").append(songDTO.getWith().getName());
        }
        return details.toString();
    }

    public Playlist searchAndProcessTracks(String setlistId, String artistName) {
        log.info("Initiating search and process tracks for SetlistId: {}", setlistId);
        if (fetchToken()) {
            Playlist playlist = playlistRepository.getBysetlistID(setlistId);
            if (playlist == null) {
                log.info("Fetching setlist for SetlistId: {}", setlistId);
                Setlist pullSetlist = setlistRepository.getSetlistById(setlistId);
                SetsDTO setsDTO = pullSetlist.getSets();

                log.info("Searching tracks for SetlistId: {}", setlistId);
                List<AppTrack> tracks = searchTracks(setsDTO, artistName);

                VenueDTO venue = pullSetlist.getVenue();
                String venueName = venue != null ? venue.getName() : "";
                String venueLocation = venue != null && venue.getCity() != null ? venue.getCity().getName() : "";
                String setlistName = artistName + " @ " + venueName + " in " + venueLocation + " " + pullSetlist.getEventDate();

                playlist = new Playlist();
                playlist.setName(setlistName);
                playlist.setTracks(tracks);
                playlist.setSetlistID(setlistId);
                playlist.setDescription("Playlist created using data from Setlist.FM. The tracks were pulled from the following Setlist.FM URL: " + pullSetlist.getUrl());
                log.info("Saving new playlist for SetlistId: {}", setlistId);
                playlistRepository.save(playlist);
            }
            return playlist;
        }
        log.info("Token fetch failed for SetlistId: {}", setlistId);
        return null;
    }

    public Playlist createPlaylist(String setlistId, boolean includeCovers) {
        log.info("Initiating playlist creation for SetlistId: {}", setlistId);
        if (fetchToken()) {
            Playlist playlist = fetchPlaylist(setlistId);
            if (playlist == null) {
                log.warn("No playlist found for SetlistId: {}", setlistId);
                return null;
            }

            log.info("Filtering tracks for SetlistId: {}, Include Covers: {}", setlistId, includeCovers);
            List<AppTrack> filteredTracks = includeCovers
                ? playlist.getTracks()
                : playlist.getTracks().stream().filter(track -> !track.isCover()).collect(Collectors.toList());

            playlist.setTracks(filteredTracks);
            return SpotifyPlaylist(playlist); 
        }
        log.info("Token fetch failed for playlist creation, SetlistId: {}", setlistId);
        return null;
    }

    private Playlist SpotifyPlaylist(Playlist prototypePlaylist) {
        log.info("Initiating Spotify playlist creation for SetlistId: {}", prototypePlaylist.getSetlistID());
        List<String> songs = new ArrayList<>();
        try {
            CreatePlaylistRequest createPlaylist = spotifyApi.createPlaylist("31fht62ert5mwjiajazfyuqf2dhm", prototypePlaylist.getName())
                                                            .public_(true)
                                                            .description(prototypePlaylist.getDescription())
                                                            .build();
            log.info("Creating Spotify playlist: {}", prototypePlaylist.getName());
            se.michaelthelin.spotify.model_objects.specification.Playlist completePlaylist = createPlaylist.execute();
            
            for (AppTrack song : prototypePlaylist.getTracks()) {
                if (song.isTrackFound()) {
                    songs.add(song.getSongUri());
                }
            }

            String[] songsArray = songs.toArray(new String[0]);
            AddItemsToPlaylistRequest addTracksToPlaylist = spotifyApi.addItemsToPlaylist(completePlaylist.getId(), songsArray).build();
            log.info("Adding tracks to Spotify playlist, SetlistId: {}", prototypePlaylist.getSetlistID());
            SnapshotResult results = addTracksToPlaylist.execute();
            prototypePlaylist.setPlaylist_id(completePlaylist.getId());
            prototypePlaylist.setSpotifyUrl(completePlaylist.getExternalUrls().get("spotify"));
            log.info("Saving playlist with Spotify integration, SetlistId: {}", prototypePlaylist.getSetlistID());
            playlistRepository.save(prototypePlaylist);
            log.info("Spotify Playlist created successfully: {}", completePlaylist.getId());

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error during Spotify playlist creation: {}", e.getMessage());
        }
        return prototypePlaylist;
    }
    
}
