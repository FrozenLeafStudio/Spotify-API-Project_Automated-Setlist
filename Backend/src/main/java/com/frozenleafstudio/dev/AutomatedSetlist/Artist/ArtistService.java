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


    public List<Artist> AllArtists(){
        return artistRepository.findAll();

    }
    public Optional<Artist> singleArtist(String n){
        // Normalize the artist name before querying
    String normalizedArtistName = n.toLowerCase();
        
        return artistRepository.findArtistByName(normalizedArtistName);
    }
    
    public void saveArtist(Artist newArtist){
        newArtist.setName(newArtist.getName().toLowerCase());
        Optional<Artist> existingArtist = artistRepository.findByName(newArtist.getName());
        if(existingArtist.isPresent()){
            return;
        }
        artistRepository.save(newArtist);
    }

    // Handle the encoding in a separate method to simplify the main logic.
    private String encodeArtistName(String artistName) {
        try {
            return URLEncoder.encode(artistName, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported Encoding Exception", e);
            return ""; 
        }
    }

    public Optional<Artist> searchArtistOnSetlist(String artistName) {
        Optional<Artist> artistInDb = singleArtist(artistName);
        System.out.println("From searchArtistOnSetlist func: "+artistName);
        if (artistInDb.isPresent()) {
            return artistInDb;
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", apiKey);
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Encoding artistname in a separate function. I ran into a problem where spaces in artist names weren't being encoded properly, if I hardcoded spaces to be + instead of %20, there was no problem with the httprequest. 
            String encodedArtistName = encodeArtistName(artistName);
            System.out.println("From searchArtistOnSetlist func after encoding: "+encodedArtistName);
            if (encodedArtistName.isEmpty()) {
                return Optional.empty(); // or handle the error accordingly.
            }

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(setlistApiUrl + "/search/artists")
                    .queryParam("artistName", encodedArtistName)
                    .queryParam("p", "1")
                    .queryParam("sort", "relevance");

            String url = builder.build().toUri().toString();
            System.out.println("URl from searchArtistOnSetlist: "+url);


            try {
                ResponseEntity<ArtistSearchResponse> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, ArtistSearchResponse.class);

                if (response != null && response.getBody() != null && !response.getBody().getArtist().isEmpty()) {
                    Artist artistFromApi = mapApiResultToArtistEntity(response.getBody().getArtist().get(0));
                    saveArtist(artistFromApi);
                    return Optional.of(artistFromApi);
                    }
                }catch (HttpClientErrorException e) {
                        log.error("Client error for artist search {}: Status Code: {}, Headers: {}, Response Body: {}", 
                            artistName, e.getStatusCode(), e.getResponseHeaders(), e.getResponseBodyAsString(), e);
                } catch (HttpServerErrorException e) {
                        log.error("Server error for artist search {}: Status Code: {}, Response Headers: {}, Response Body: {}", 
                            artistName, e.getStatusCode(), e.getResponseHeaders(), e.getResponseBodyAsString(), e);
                } catch (RestClientException e) {
                        log.error("RestClientException on search for Artist  {}: {}", artistName, e.getMessage(), e);
                }
                return Optional.empty();
            }
            
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
