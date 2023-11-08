package com.frozenleafstudio.dev.AutomatedSetlist.Artist;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import com.frozenleafstudio.dev.AutomatedSetlist.dto.ArtistSearchResponse;
import com.frozenleafstudio.dev.AutomatedSetlist.dto.ArtistSearchResult;


@Service
public class ArtistService {
    @Autowired
    private ArtistRepo artistRepository;
    @Value("${setlist.api.key}")
    private String apiKey; // set to env var in application.properties
    private final RestTemplate restTemplate;
    
    public ArtistService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<Artist> AllArtists(){
        return artistRepository.findAll();

    }
    public Optional<Artist> singleArtist(String id){
        return artistRepository.findArtistByMbid(id);
    }

    // This method searches for an artist on setlist.fm by name; after testing plain api call, switch return object to Optional<ArtistSearchResult>
    public String searchArtistOnSetlist(String artistName) {
        System.out.println("from searchArtistOnSetlist method: " + artistName);
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "https://api.setlist.fm/rest/1.0/search/artists?artistName=" + 
                     UriUtils.encodePath(artistName, "UTF-8") + "&p=1&sort=relevance";
        try{
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        }catch(Exception e){
            e.printStackTrace();
            return "Error during setlist.FM API call: " + e.getMessage();
        }

        /* Prototyping data handling via DTO classes 
        
        ResponseEntity<ArtistSearchResponse> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, ArtistSearchResponse.class);

        System.out.println("from searchArtistOnSetlist method: " + response);

        if (response.getBody() != null && !response.getBody().getArtistList().isEmpty()) {
            return Optional.of(response.getBody().getArtistList().get(0));
        }
        return Optional.empty();
            // Code to call setlist.fm API and retrieve the search results */
    }

    /* @Value("${setlist.api.key}")
    private String apiKey; // You can store your API key in application.properties or a secure configuration

    private final String setlistApiUrl = "https://api.setlist.fm/rest/1.0";

    private final RestTemplate restTemplate;

    public ArtistService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> searchArtists(String artistName) {
        String url = setlistApiUrl + "/search/artists?artistName=" + artistName;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response;
    } */
}
