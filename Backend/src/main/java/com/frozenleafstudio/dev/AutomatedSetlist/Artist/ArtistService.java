package com.frozenleafstudio.dev.AutomatedSetlist.Artist;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ArtistService {
    @Autowired
    private ArtistRepo artistRepository;
    public List<Artist> AllArtists(){
        return artistRepository.findAll();

    }
    public Optional<Artist> singleArtist(String id){
        return artistRepository.findArtistByMbid(id);
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
