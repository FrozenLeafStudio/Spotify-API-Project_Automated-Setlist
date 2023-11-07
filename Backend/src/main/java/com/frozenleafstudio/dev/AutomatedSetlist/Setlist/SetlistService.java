package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SetlistService {

    @Value("${setlist.api.key}")
    private String apiKey; // You can store your API key in application.properties or a secure configuration

    private final String setlistApiUrl = "https://api.setlist.fm/rest/1.0";

    private final RestTemplate restTemplate;

    public SetlistService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> searchSetlists(String artistName, String date) {
        String url = setlistApiUrl + "/search/setlists?artistName=" + artistName + "&date=" + date;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response;
    }

    // You can add more methods for different Setlist.fm API endpoints as needed

    // Remember to handle exceptions, error handling, and parsing the JSON response appropriately.
}
