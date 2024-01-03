package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frozenleafstudio.dev.AutomatedSetlist.Artist.Artist;
import com.frozenleafstudio.dev.AutomatedSetlist.Artist.ArtistRepo;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs.SetDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs.SetlistDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs.SetlistFilterResponse;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs.SetsDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs.SongDTO;

/* 
TODOs:
Batch jobs - improve wait times by batching setlist.fm response parsing into phases. Return partial to frontend and allow backend to continue processing.
User Testing: Display full setlist details in the description. Additionally, if an artist COVERS a song during a set, 
the user may want to know what songs they can expect - ADD: new "CoverBand" object to catch cases where JSON response for setlist tracks 
include cover songs (currently, I'm fetching the song name but the cover songs also include an array of objects) 

IMPORTANT NOTE: Running into timeout errors when returning setlists for old artists - switching to single page return for now.
    final int itemsPerPage = 20; // Assuming the API always returns 20 items per page
    int page = 1;
    int totalRecords = 0;
    boolean morePagesAvailable;

    do {
        ResponseEntity<String> response = fetchSetlistPageAsString(artistMbid, page);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            SetlistFilterResponse setlistResponse = parseJsonToSetlistFilterResponse(response.getBody());
            List<SetlistDTO> setlists = setlistResponse.getSetlist();

            for (SetlistDTO setlist : setlists) {
                if (setlist.getEventLocalDate().isAfter(startdate) && !isSongListEmpty(setlist)) {
                    processedSetlists.add(setlist);
                }
            }

            if (page == 1) {
                totalRecords = setlistResponse.getTotal();
            }

            morePagesAvailable = page * itemsPerPage < totalRecords;
        } else {
            morePagesAvailable = false;
        }
        page++;
    } while (morePagesAvailable);
*/

@Service
public class SetlistService {
    @Autowired
    private SetlistRepo setlistRepository;
    @Autowired
    private ArtistRepo artistRepository;

    @Value("${setlist.api.key}")
    private String apiKey; 


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
    public List<Setlist> getSetlistByArtist(String mbid){
        return setlistRepository.findSetlistsByArtistMbid(mbid);
    }
    private void updateArtistWithSetlists(String artistMbid, List<Setlist> newSetlists) {
        Optional<Artist> artistOpt = artistRepository.findByMbid(artistMbid);

        if (artistOpt.isPresent()) {
            Artist artist = artistOpt.get();
            List<Setlist> existingSetlists = artist.getSetlists();
            if (existingSetlists == null) {
                existingSetlists = new ArrayList<>();
            }
            existingSetlists.addAll(newSetlists);
            artist.setSetlists(existingSetlists);
            artistRepository.save(artist);
        }
    }
    //method to initiate setlist searching & filtering
    public List<Setlist> searchAndProcessArtistSetlists(String artistMbid, LocalDate startdate){
        List<Setlist> setlistInDb = getSetlistByArtist(artistMbid);
        if(!setlistInDb.isEmpty()){
            return setlistInDb;
        }
        List<SetlistDTO> recentSetlists = filterRecentSetlists(artistMbid, startdate);

        //convert SetlistDTO to Setlist model and save to database
        List<Setlist> modelSetlist = recentSetlists.stream()
                                            .map(this::convertToDomainModel)
                                            .collect(Collectors.toList());

        setlistRepository.saveAll(modelSetlist);
        // Update Artist with new Setlists
        updateArtistWithSetlists(artistMbid, modelSetlist);
        return modelSetlist;
    }
    
    //method to filter and collect setlist event IDs and dates
    private List<SetlistDTO> filterRecentSetlists(String artistMbid, LocalDate startdate) {
    List<SetlistDTO> processedSetlists = new ArrayList<>();
    ResponseEntity<String> response = fetchSetlistPageAsString(artistMbid, 1); // page number 1
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            SetlistFilterResponse setlistResponse = parseJsonToSetlistFilterResponse(response.getBody());
            List<SetlistDTO> setlists = setlistResponse.getSetlist();

            for (SetlistDTO setlist : setlists) {
                if (setlist.getEventLocalDate().isAfter(startdate) && !isSongListEmpty(setlist)) {
                    processedSetlists.add(setlist);
                }
            }
        }
    return processedSetlists;
} 

    private boolean isSongListEmpty(SetlistDTO setlist) {
        if(setlist.getSets() == null || setlist.getSets().getSet().isEmpty()){
            return true;
        }
        return setlist.getSets().getSet().stream()
                    .noneMatch(set -> set.getSong().stream()
                                        .anyMatch(song->song.getName()!= null && !song.getName().isEmpty()));
    }

    private SetlistFilterResponse parseJsonToSetlistFilterResponse(String body) {
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.readValue(body, SetlistFilterResponse.class);
        } catch (IOException e){
            log.error("Error parsing JSON", e);
            return null;
        }
    }

    //helper method to make an API call for specific page of setlists
    private ResponseEntity<String> fetchSetlistPageAsString(String artistMbid, int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(setlistApiUrl + "/search/setlists")
                                                            .queryParam("artistMbid",artistMbid)
                                                            .queryParam("p", page);

        String url = builder.build().toUri().toString();
        try{
            return makeApiCallWithRetry(url, entity, 5, 1000);
        }catch (HttpClientErrorException e) {
                log.error("Client error for artist Setlist search {}: Status Code: {}, Headers: {}, Response Body: {}", 
                    artistMbid, e.getStatusCode(), e.getResponseHeaders(), e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
                log.error("Server error for artist Setlist search {}: Status Code: {}, Response Headers: {}, Response Body: {}", 
                    artistMbid, e.getStatusCode(), e.getResponseHeaders(), e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
                log.error("RestClientException on Setlist search for Artist  {}: {}", artistMbid, e.getMessage(), e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during API call");
    }
    //new method to avoid 429 too many requests; in the future, I'll look into using a library.
    private ResponseEntity<String> makeApiCallWithRetry(String url, HttpEntity<String> entity, int maxRetries, long delay) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            } catch (HttpClientErrorException.TooManyRequests ex) {
                attempt++;
                try {
                    Thread.sleep(delay * (long) Math.pow(2, attempt));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Thread interrupted during retry backoff", ie);
                }
            }
        }
        throw new RuntimeException("Failed to make API call after " + maxRetries + " retries");
    }

    private Setlist convertToDomainModel(SetlistDTO dto) {
        Setlist setlist = new Setlist();
        setlist.setSetlistID(dto.getId());
        setlist.setEventDate(dto.getEventDate());
        setlist.setArtist(dto.getArtist());
        setlist.setVenue(dto.getVenue());
        if (dto.getTour() != null) {
            setlist.setTourName(dto.getTour().getName());
        } else {
            setlist.setTourName(null);
        }
        setlist.setUrl(dto.getUrl());

        // Initialize SetsDTO
        SetsDTO setsDTO = new SetsDTO();
        List<SetDTO> setList = new ArrayList<>();
        
        //rather than assigning setDTO a variable for the encore (since setlist.fm returns encore as a set with a value "encore" instead of "name"), we will check if last set is encore
        List<SetDTO> dtoSets = dto.getSets().getSet();
        int lastSetIndex = dtoSets.size() - 1;

        for (int i = 0; i < dtoSets.size(); i++) {
            SetDTO setDTO = dtoSets.get(i);

            // Check if there's more than one set and the last set is unnamed
            if (dtoSets.size() > 1 && i == lastSetIndex && setDTO.getName() == null) {
                setDTO.setName("Encore");
            }

            List<SongDTO> processedSongs = new ArrayList<>();
            for (SongDTO song : setDTO.getSong()) {
                processedSongs.add(song);
            }
            setDTO.setSong(processedSongs);
            setList.add(setDTO);
        }

        setsDTO.setSet(setList);
        setlist.setSets(setsDTO);

        return setlist;
    }

    public Setlist getSetlistById(String setlistId) {
        return setlistRepository.findSetlistBySetlistID(setlistId);
    }
}
