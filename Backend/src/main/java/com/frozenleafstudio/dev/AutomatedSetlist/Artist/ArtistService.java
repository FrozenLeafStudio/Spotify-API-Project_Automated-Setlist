package com.frozenleafstudio.dev.AutomatedSetlist.Artist;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.ArtistDTOs.ArtistSearchResponse;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.ArtistDTOs.ArtistSearchResult;


@Service
public class ArtistService {
    private final ArtistRepo artistRepository;
    private final String apiKey;
    private final String setlistApiUrl;
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(ArtistService.class);

    public ArtistService(ArtistRepo artistRepository, 
                         @Value("${setlist.api.key}") String apiKey, 
                         RestTemplate restTemplate) {
        this.artistRepository = artistRepository;
        this.apiKey = apiKey;
        this.setlistApiUrl = "https://api.setlist.fm/rest/1.0";
        this.restTemplate = restTemplate;
    }

    // Retrieve all artists from the database
    public List<Artist> allArtists() {
        return artistRepository.findAll();
    }

    // Find a single artist by name, with name normalization
    public Optional<Artist> singleArtist(String name) {
        return artistRepository.findArtistByName(name.toLowerCase());
    }

    // Save a new artist, ensuring uniqueness by name and mbid
public void saveArtist(Artist newArtist) {
    String normalizedName = newArtist.getName().toLowerCase();
    newArtist.setName(normalizedName);

    artistRepository.findByNameAndMbid(normalizedName, newArtist.getMbid())
        .ifPresentOrElse(
            existingArtist -> log.info("Artist already exists: {}", newArtist.getName()),
            () -> artistRepository.save(newArtist)
        );
}

    // Encode the artist name for URL usage
    private String encodeArtistName(String artistName) {
        try {
            return URLEncoder.encode(artistName, StandardCharsets.UTF_8.name())
                             .replace("%25", "%");
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported Encoding Exception", e);
            return "";
        }
    }
    public List<Artist> getAtoZArtists() {
        char alphabet;
        List<Artist> artists = new ArrayList<>();
        for(alphabet = 'A'; alphabet <= 'Z'; alphabet++)
        {
            String currentLetter = String.valueOf(alphabet);
            Optional<Artist> artistSeedArray = fetchArtistsFromApi(currentLetter, true);
            artistSeedArray.ifPresent(artists::add);
        };
        return artists;
    }

    // Search for an artist, first in the DB, then via API if not found
    public Optional<Artist> searchArtistOnSetlist(String artistName) {
        return singleArtist(artistName)
            .or(() -> fetchArtistsFromApi(artistName, false));
    }
    
    // Fetch artist details from the external API
    private Optional<Artist> fetchArtistsFromApi(String artistName, boolean fetchMultiple) {
        String encodedArtistName = encodeArtistName(artistName);
        if (encodedArtistName.isEmpty() || (fetchMultiple && encodedArtistName.length() > 1)) {
            return Optional.empty();
        }

        String url = setlistApiUrl + "/search/artists?artistName=" + encodedArtistName + "&p=1&sort=relevance";

        try {
            URI uri = new URI(url);
            ResponseEntity<ArtistSearchResponse> response = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(createHeaders()), ArtistSearchResponse.class);
            List<ArtistSearchResult> artistList = response.getBody()!= null ?
                            response.getBody().getArtist() : Collections.emptyList();
            if(fetchMultiple){
            // If multiple artists are to be fetched and saved
                List<Artist> savedArtists = saveArtists(artistList);
                return savedArtists.isEmpty() ? Optional.empty() : Optional.of(savedArtists.get(0));
            } else{
                return artistList.stream()
                    .findFirst()
                    .map(this::saveArtistAndReturn);
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Artist not found in API: {}", artistName);
                return Optional.empty();  
            } else {
                log.error("Error during API call for artist: {}", artistName, e);
                return Optional.empty();  
            }
        } catch (Exception e) {
            log.error("Error during API call for artist: {}", artistName, e);
            return Optional.empty(); 
        }
    }

    private List<Artist> saveArtists(List<ArtistSearchResult> artistList) {
        return artistList.stream()
                .map(this::mapApiResultToArtistEntity)
                .peek(this::saveArtist)
                .collect(Collectors.toList());
    }
    // Create headers for the API request
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");
        return headers;
    }

    // Save the artist and return it
    private Artist saveArtistAndReturn(ArtistSearchResult artistSearchResult) {
        Artist artist = mapApiResultToArtistEntity(artistSearchResult);
        saveArtist(artist);
        return artist;
    }
            
    // Map API response to Artist entity using Jackson for improved performance
    private Artist mapApiResultToArtistEntity(ArtistSearchResult artistSearchResult) {
        ObjectMapper mapper = new ObjectMapper();
        
        return mapper.convertValue(artistSearchResult, Artist.class);
    }

}
