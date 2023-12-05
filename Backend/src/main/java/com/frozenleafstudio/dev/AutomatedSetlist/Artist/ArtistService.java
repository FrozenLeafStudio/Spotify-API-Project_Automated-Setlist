package com.frozenleafstudio.dev.AutomatedSetlist.Artist;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
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
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.ResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.client5.http.ClientProtocolException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frozenleafstudio.dev.AutomatedSetlist.dto.ArtistSearchResponse;
import com.frozenleafstudio.dev.AutomatedSetlist.dto.ArtistSearchResult;


@Service
public class ArtistService {
    @Autowired
    private ArtistRepo artistRepository;
    @Value("${setlist.api.key}")
    private String apiKey;
    private final String setlistApiUrl = "https://api.setlist.fm/rest/1.0";
    private static final Logger log = LoggerFactory.getLogger(ArtistService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ArtistService() {
        // RestTemplate can be removed if no longer used
    }

    // Returns all artists
    public List<Artist> allArtists(){
        return artistRepository.findAll();
    }

    // Retrieves a single artist by name
    public Optional<Artist> singleArtist(String name){
        return artistRepository.findArtistByName(name.toLowerCase());
    }
    
    // Saves a new artist, avoiding duplicates
    public void saveArtist(Artist newArtist){
        newArtist.setName(newArtist.getName().toLowerCase());
        Optional<Artist> existingArtist = artistRepository.findByName(newArtist.getName());
        if(existingArtist.isPresent()){
            log.info("Artist already exists: " + newArtist.getName());
            return;
        }
        artistRepository.save(newArtist);
    }

    // Sends HTTP request and returns response as a string
    private String sendRequest(String url) {
        HttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("x-api-key", apiKey);
            httpGet.addHeader("Accept", "application/json");

            ResponseHandler<String> responseHandler = response -> {
                int status = response.getCode();
                if (status >= 200 && status < 300) {
                    return EntityUtils.toString(response.getEntity());
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            return httpclient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            log.error("IOException during HTTP call: ", e);
            return null;
        }
    }

    // Searches artist on Setlist.fm and maps API response to Artist entity
    public Optional<Artist> searchArtistOnSetlist(String artistName) {
        Optional<Artist> artistInDb = singleArtist(artistName);
        if (artistInDb.isPresent()) {
            return artistInDb;
        }

        String url = setlistApiUrl + "/search/artists?artistName=" + URLEncoder.encode(artistName, StandardCharsets.UTF_8) + "&p=1&sort=relevance";
        String response = sendRequest(url);
        if (response != null) {
            try {
                ArtistSearchResponse searchResponse = objectMapper.readValue(response, ArtistSearchResponse.class);
                if (!searchResponse.getArtist().isEmpty()) {
                    Artist artistFromApi = mapApiResultToArtistEntity(searchResponse.getArtist().get(0));
                    saveArtist(artistFromApi);
                    return Optional.of(artistFromApi);
                }
            } catch (IOException e) {
                log.error("Error parsing JSON response: ", e);
            }
        }
        return Optional.empty();
    }

    // Maps API result to Artist entity
    private Artist mapApiResultToArtistEntity(ArtistSearchResult artistSearchResult) {
        Artist artist = new Artist();
        artist.setMbid(artistSearchResult.getMbid());
        artist.setName(artistSearchResult.getName());
        artist.setSortName(artistSearchResult.getSortName());
        artist.setDisambiguation(artistSearchResult.getDisambiguation());
        artist.setUrl(artistSearchResult.getUrl());
        artist.setTmid(artistSearchResult.getTmid() != null ? artistSearchResult.getTmid() : null);
        artist.setSetlists(new ArrayList<>());
        return artist;
    }
}
