package com.frozenleafstudio.dev.AutomatedSetlist.Artist;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.frozenleafstudio.dev.AutomatedSetlist.dto.ArtistSearchResponse;
import com.frozenleafstudio.dev.AutomatedSetlist.dto.ArtistSearchResult;


@Service
public class ArtistService {
    @Autowired
    private ArtistRepo artistRepository;
    @Value("${setlist.api.key}")
    private String apiKey; // set to env var in application.properties
    private final String setlistApiUrl = "https://api.setlist.fm/rest/1.0";
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(ArtistService.class);

    
    public ArtistService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<Artist> allArtists() {
        return artistRepository.findAll();
    }

    public Optional<Artist> singleArtist(String n) {
        return artistRepository.findArtistByName(n.toLowerCase());
    }
    
    public void saveArtist(Artist newArtist) {
        newArtist.setName(newArtist.getName().toLowerCase());
        Optional<Artist> existingArtist = artistRepository.findByName(newArtist.getName());
        if (existingArtist.isPresent()) {
            // Artist already exists, no need to save again
            return;
        }
        artistRepository.save(newArtist);
    }

    // Handle the encoding in a separate method to simplify the main logic.
    private String encodeArtistName(String artistName) {
        try {
            String encodedArtistName = URLEncoder.encode(artistName, StandardCharsets.UTF_8.name());
            // Handle double encoding issue
            return encodedArtistName.replace("%25", "%");
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported Encoding Exception", e);
            return ""; 
        }
    }

    public Optional<Artist> searchArtistOnSetlist(String artistName) {
        Optional<Artist> artistInDb = singleArtist(artistName);
        if (artistInDb.isPresent()) {
            return artistInDb;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String encodedArtistName = encodeArtistName(artistName);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(setlistApiUrl + "/search/artists")
                .queryParam("artistName", encodedArtistName)
                .queryParam("p", "1")
                .queryParam("sort", "relevance");

        String url = builder.build().toUri().toString();

        try {
            ResponseEntity<ArtistSearchResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ArtistSearchResponse.class);
            if (response.getBody() != null && !response.getBody().getArtist().isEmpty()) {
                Artist artistFromApi = mapApiResultToArtistEntity(response.getBody().getArtist().get(0));
                saveArtist(artistFromApi);
                return Optional.of(artistFromApi);
            }
        } catch (RestClientException e) {
            log.error("Error during API call for artist '{}': {}", artistName, e.getMessage());
        }
        return Optional.empty();
    }

    //mapping API JSON returned from DTO to model values and returning artist to be saved in MongoDB
    private Artist mapApiResultToArtistEntity(ArtistSearchResult artistSearchResult) {
        Artist artist = new Artist();
        artist.setMbid(artistSearchResult.getMbid());
        artist.setName(artistSearchResult.getName());
        artist.setSortName(artistSearchResult.getSortName());
        artist.setDisambiguation(artistSearchResult.getDisambiguation());
        artist.setUrl(artistSearchResult.getUrl());

        // Check if tmid is present and only set it if it's not null
        if (artistSearchResult.getTmid() != null) {
            artist.setTmid(artistSearchResult.getTmid());
        }else{
            artist.setTmid(null);
        }

        /* // Initially, the setlists will be empty or null until they are populated later
        List<Setlist> setlists = new ArrayList<>();
        // populate setlists list
        artist.setSetlists(setlists); */
        artist.setSetlists(new ArrayList<>());

        return artist;
    }
}
