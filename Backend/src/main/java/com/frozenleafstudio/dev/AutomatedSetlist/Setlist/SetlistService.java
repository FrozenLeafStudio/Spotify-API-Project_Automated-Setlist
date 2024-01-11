package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.SetDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.SetlistDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.SetlistFilterResponse;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.SetsDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.Util.DateUtil;

@Service
public class SetlistService {
    private final RestTemplate restTemplate;
    private final SetlistRepo setlistRepository;
    private final String setlistApiUrl = "https://api.setlist.fm/rest/1.0";
    private final Logger log = LoggerFactory.getLogger(SetlistService.class);

    @Value("${setlist.api.key}")
    private String apiKey; 

    public SetlistService(RestTemplate restTemplate, SetlistRepo setlistRepository) {
        this.restTemplate = restTemplate;
        this.setlistRepository = setlistRepository;
    }

    public Setlist getSetlistById(String setlistId) {
        log.info("Fetching setlist by ID: {}", setlistId);
        return setlistRepository.findSetlistBySetlistID(setlistId);
    }

    public void deleteAllSetlists() {
        setlistRepository.deleteAll();
    }

    public List<Setlist> fetchAndProcessArtistSetlists(String artistMbid, int pageNumber) {
        SetlistFilterResponse setlistFilterResponse = fetchSetlistPage(artistMbid, pageNumber);
        List<SetlistDTO> setlistDTOs = setlistFilterResponse.getSetlist();

        List<Setlist> processedSetlists = new ArrayList<>();
        for (SetlistDTO dto : setlistDTOs) {
            if (!isSongListEmpty(dto)) {
                Setlist existingSetlist = setlistRepository.findSetlistBySetlistID(dto.getId());
                if (existingSetlist == null) {
                    Setlist setlist = convertToDomainModel(dto);
                    processedSetlists.add(setlistRepository.save(setlist));
                } else {
                    processedSetlists.add(existingSetlist);
                }
            }
        }
        return processedSetlists;
    }

    private SetlistFilterResponse fetchSetlistPage(String artistMbid, int pageNumber) {
        ResponseEntity<String> response = fetchSetlistPageAsString(artistMbid, pageNumber);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return parseJsonToSetlistFilterResponse(response.getBody());
        } else {
            log.error("Error fetching setlists: {}", response.getStatusCode());
            return new SetlistFilterResponse();
        }
    }

    private ResponseEntity<String> fetchSetlistPageAsString(String artistMbid, int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(setlistApiUrl + "/search/setlists")
                                                            .queryParam("artistMbid", artistMbid)
                                                            .queryParam("p", page);
        return makeApiCallWithRetry(builder.build().toUri().toString(), entity);
    }

    private ResponseEntity<String> makeApiCallWithRetry(String url, HttpEntity<String> entity) {
        int maxRetries = 5;
        long delay = 1000;
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            } catch (HttpClientErrorException.TooManyRequests ex) {
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

    private SetlistFilterResponse parseJsonToSetlistFilterResponse(String body) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(body, SetlistFilterResponse.class);
        } catch (IOException e) {
            log.error("Error parsing JSON", e);
            return null;
        }
    }

    private boolean isSongListEmpty(SetlistDTO setlist) {
        if (setlist.getSets() == null || setlist.getSets().getSet().isEmpty()) {
            return true;
        }
        return setlist.getSets().getSet().stream()
                      .noneMatch(set -> set.getSong().stream()
                                           .anyMatch(song -> song.getName() != null && !song.getName().isEmpty()));
    }


    private Setlist convertToDomainModel(SetlistDTO dto) {
        //log.info("Converting SetlistDTO to domain model, Setlist ID: {}", dto.getId());
        Setlist setlist = new Setlist();
        setlist.setSetlistID(dto.getId());
        LocalDate eventDate = DateUtil.parseDate(dto.getEventDate());
        setlist.setEventDate(DateUtil.formatDate(eventDate));
        setlist.setArtist(dto.getArtist());
        setlist.setVenue(dto.getVenue());
        setlist.setTourName(dto.getTour() != null ? dto.getTour().getName() : null);
        setlist.setUrl(dto.getUrl());

        SetsDTO setsDTO = convertSetsDtoToDomain(dto.getSets());
        setlist.setSets(setsDTO);

        return setlist;
    }

    private SetsDTO convertSetsDtoToDomain(SetsDTO setsDto) {
        //log.info("Converting SetsDTO to domain model");
        SetsDTO setsDomain = new SetsDTO();
        List<SetDTO> setList = new ArrayList<>();

        if (setsDto != null) {
            for (SetDTO setDto : setsDto.getSet()) {
                SetDTO setDomain = new SetDTO();
                setDomain.setName(setDto.getName());
                setDomain.setSong(setDto.getSong().stream().collect(Collectors.toList()));
                setList.add(setDomain);
            }
        }

        setsDomain.setSet(setList);
        return setsDomain;
    }
}