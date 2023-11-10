package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.text.html.Option;

import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.frozenleafstudio.dev.AutomatedSetlist.dto.ArtistSearchResponse;
import com.frozenleafstudio.dev.AutomatedSetlist.dto.SetlistFilter;
import com.frozenleafstudio.dev.AutomatedSetlist.dto.SetlistFilterResponse;

@Service
public class SetlistService {
    @Autowired
    private SetlistRepo setlistRepository;

    @Value("${setlist.api.key}")
    private String apiKey; // You can store your API key in application.properties or a secure configuration
    private int requestCount = 0;
    private long lastRequestTime = System.currentTimeMillis();

    private final String setlistApiUrl = "https://api.setlist.fm/rest/1.0";

    private final RestTemplate restTemplate;
        private static final Logger log = LoggerFactory.getLogger(SetlistService.class);


    public SetlistService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public ResponseEntity<String> searchSetlists(String artistName, String date) {
        String url = setlistApiUrl + "/search/setlists?artistName=" + artistName + "&date=" + date;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response;
    }
    public List<Setlist> getSetlist(String mbid){
        return setlistRepository.findSetlistByMbid(mbid);
    }
    private synchronized void RateLimit(){

        try{
            Thread.sleep(2000);
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
        /* if(requestCount == 2){
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastRequestTime;

            if(elapsedTime < 1000){
                try{
                    Thread.sleep(1000 - elapsedTime);
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
            requestCount = 0;
            lastRequestTime = System.currentTimeMillis();
        }
        requestCount++; */
    }

    //method to initiate setlist searching & filtering
    public List<Setlist> searchAndProcessArtistSetlists(String artistMbid, LocalDate startdate){
        List<Setlist> setlistInDb = getSetlist(artistMbid);
        if(!setlistInDb.isEmpty()){
            return setlistInDb;
        }
        //Step 1: Filter recent setlists
        List<SetlistFilter> recentSetlists = filterRecentSetlists(artistMbid, startdate);

        //Step 2: Retrieve detailed data for filtered setlists and save to database
        List<String> eventIds = recentSetlists.stream()
                                        .map(SetlistFilter::getId)
                                        .collect(Collectors.toList());
        return retrieveSetlistDetails(eventIds);
    }
    private List<Setlist> retrieveSetlistDetails(List<String> eventIds) {
        List<Setlist> setlists = new ArrayList<>();
        for(String eventId : eventIds){
            ResponseEntity<Setlist> response = fetchSetlistDetails(eventId);
            if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null){
                setlists.add(response.getBody());
            }
        }
        // Save all the details to the database
        setlistRepository.saveAll(setlists);
        return setlists;
    }

    //method to filter and collect setlist event IDs and dates
    private List<SetlistFilter> filterRecentSetlists(String artistMbid, LocalDate startdate) {
        List<SetlistFilter> filteredSetlists = new ArrayList<>();
        int page = 1;
        int itemsPerPage = 20;
        int totalRecords = 0;
        boolean morePagesAvailable;

        do{
            ResponseEntity<SetlistFilterResponse> response = fetchSetlistPage(artistMbid, page);
            if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null){
                List<SetlistFilter> setlists = response.getBody().getSetlist();
                setlists.stream().filter(s -> s.getEventLocalDate().isAfter(startdate)).forEach(filteredSetlists::add);
                if(page == 1){
                totalRecords = response.getBody().getTotal();
                }
            morePagesAvailable = page * itemsPerPage < totalRecords;
            }else{
                morePagesAvailable = false;   
            }
            page++;
        } while(morePagesAvailable);
        
        return filteredSetlists;
    }

    //helper method to make an API call for specific page of setlists
    private ResponseEntity<SetlistFilterResponse> fetchSetlistPage(String artistMbid, int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(setlistApiUrl + "/search/setlists")
                                                            .queryParam("artistMbid",artistMbid)
                                                            .queryParam("p", page);

        String url = builder.build().toUri().toString();
        RateLimit();
        return restTemplate.exchange(url, HttpMethod.GET, entity, SetlistFilterResponse.class);
    }
    //helper method to make an API call for detailed setlist data
    private ResponseEntity<Setlist> fetchSetlistDetails(String eventIds) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = setlistApiUrl + "/setlist/" + eventIds;

        RateLimit();
        return restTemplate.exchange(url, HttpMethod.GET, entity, Setlist.class);
        }
}
