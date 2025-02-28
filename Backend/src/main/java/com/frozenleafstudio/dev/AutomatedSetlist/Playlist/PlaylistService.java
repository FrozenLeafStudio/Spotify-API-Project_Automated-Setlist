package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.SetDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.SetsDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.SongDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.VenueDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.Setlist.Setlist;
import com.frozenleafstudio.dev.AutomatedSetlist.Setlist.SetlistService;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.vavr.control.Try;
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
    private final RateLimiter spotifyRateLimiter;

    @Autowired
    public PlaylistService(SpotifyApi spotifyApi, SpotifyTokenService spotifyTokenService, SetlistService setlistRepository, PlaylistRepo playlistRepository) {
        this.spotifyApi = spotifyApi;
        this.spotifyTokenService = spotifyTokenService;
        this.setlistRepository = setlistRepository;
        this.playlistRepository = playlistRepository;
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(500))
                .build();
        this.spotifyRateLimiter = RateLimiter.of("spotifyApi", config);
    }

    private Playlist fetchPlaylist(String setlistId) {
        return playlistRepository.getBysetlistID(setlistId);
    }

    private boolean fetchToken() {
        String checkToken = this.spotifyTokenService.getCurrentAccessToken();
        return !checkToken.isEmpty();
    }

    private List<AppTrack> searchTracks(SetsDTO setsDTO, String artistName) {
        if (setsDTO == null) {
            return Collections.emptyList();
        }
        List<CompletableFuture<AppTrack>> futures = setsDTO.getSet().stream()
                .flatMap(set -> set.getSong().stream().map(song -> processSong(song, artistName, set)))
                .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    private CompletableFuture<AppTrack> processSong(SongDTO song, String artistName, SetDTO set) {
        boolean isTape = song.getTape() != null && song.getTape();
        boolean isCover = song.getCover() != null;
        if (isTape) {
            return CompletableFuture.completedFuture(new AppTrack(false, null, song.getName(), artistName, null, null,
                    createDetailsString(song, artistName, isCover), true, false));
        } else {
            return searchSpotifyForTrack(song, artistName, isCover);
        }
    }

    @Async
    protected CompletableFuture<AppTrack> searchSpotifyForTrack(SongDTO song, String artistName, boolean isCover) {
        Supplier<AppTrack> supplier = () -> searchSpotifyAndCreateAppTrack(song.getName(), artistName, song, isCover);
        Supplier<AppTrack> rateLimitedSupplier = RateLimiter.decorateSupplier(spotifyRateLimiter, supplier);
        AppTrack appTrack = Try.ofSupplier(rateLimitedSupplier)
                .getOrElseGet(throwable -> {
                    log.error("Rate limiter error: {}", throwable.getMessage());
                    return new AppTrack(false, null, song.getName(), artistName, null, null,
                            createDetailsString(song, artistName, isCover), false, isCover);
                });
        return CompletableFuture.completedFuture(appTrack);
    }

    private AppTrack searchSpotifyAndCreateAppTrack(String trackName, String artistName, SongDTO song, boolean isCover) {
        String query = trackName + " artist:" + artistName;
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
        String albumImageUrl = spotifyTrack.getAlbum().getImages().length > 0 ? spotifyTrack.getAlbum().getImages()[0].getUrl() : null;
        return new AppTrack(trackStatus, spotifyTrack.getUri(), spotifyTrack.getName(), spotifyTrack.getArtists()[0].getName(),
                spotifyTrack.getAlbum().getName(), albumImageUrl, createDetailsString(songDTO, artistName, isCover),
                songDTO.getTape() != null && songDTO.getTape(), isCover);
    }

    private String createDetailsString(SongDTO songDTO, String artistName, boolean isCover) {
        StringBuilder details = new StringBuilder();
        if (songDTO.getInfo() != null) {
            details.append(songDTO.getInfo());
        }
        if (isCover) {
            if (details.length() > 0)
                details.append("; ");
            details.append(songDTO.getCover().getName()).append("'s song: ").append(songDTO.getName());
        }
        if (songDTO.getWith() != null) {
            if (details.length() > 0)
                details.append("; ");
            details.append("Featuring: ").append(songDTO.getWith().getName());
        }
        return details.toString();
    }

    public Playlist searchAndProcessTracks(String setlistId, String artistName) {
        if (fetchToken()) {
            Playlist playlist = playlistRepository.getBysetlistID(setlistId);
            if (playlist == null) {
                Setlist pullSetlist = setlistRepository.getSetlistById(setlistId);
                SetsDTO setsDTO = pullSetlist.getSets();
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
                playlistRepository.save(playlist);
            }
            return playlist;
        }
        log.info("Token fetch failed for SetlistId: {}", setlistId);
        return null;
    }

    public Playlist createPlaylist(String setlistId, boolean includeCovers) {
        if (fetchToken()) {
            Playlist playlist = fetchPlaylist(setlistId);
            if (playlist == null) {
                log.warn("No playlist found for SetlistId: {}", setlistId);
                return null;
            }
            List<AppTrack> filteredTracks = includeCovers ? playlist.getTracks() : playlist.getTracks().stream().filter(track -> !track.isCover()).collect(Collectors.toList());
            playlist.setTracks(filteredTracks);
            return SpotifyPlaylist(playlist);
        }
        log.info("Token fetch failed for playlist creation, SetlistId: {}", setlistId);
        return null;
    }

    private Playlist SpotifyPlaylist(Playlist prototypePlaylist) {
        List<String> songs = new ArrayList<>();
        try {
            CreatePlaylistRequest createPlaylist = spotifyApi.createPlaylist("31fht62ert5mwjiajazfyuqf2dhm", prototypePlaylist.getName())
                    .public_(true)
                    .description(prototypePlaylist.getDescription())
                    .build();
            se.michaelthelin.spotify.model_objects.specification.Playlist completePlaylist = createPlaylist.execute();
            for (AppTrack song : prototypePlaylist.getTracks()) {
                if (song.isTrackFound()) {
                    songs.add(song.getSongUri());
                }
            }
            String[] songsArray = songs.toArray(new String[0]);
            AddItemsToPlaylistRequest addTracksToPlaylist = spotifyApi.addItemsToPlaylist(completePlaylist.getId(), songsArray).build();
            SnapshotResult results = addTracksToPlaylist.execute();
            prototypePlaylist.setPlaylist_id(completePlaylist.getId());
            prototypePlaylist.setSpotifyUrl(completePlaylist.getExternalUrls().get("spotify"));
            playlistRepository.save(prototypePlaylist);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error during Spotify playlist creation: {}", e.getMessage());
        }
        return prototypePlaylist;
    }
}
