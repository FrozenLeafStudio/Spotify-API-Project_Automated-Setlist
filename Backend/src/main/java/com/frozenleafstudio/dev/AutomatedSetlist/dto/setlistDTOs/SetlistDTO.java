package com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetlistDTO {
    private String id; // setlistID
    private String eventDate;
    private ArtistDTO artist; // Nested DTO for artist
    private VenueDTO venue;   // Nested DTO for venue
    private TourDTO tour;
    private SetsDTO sets;     // Nested DTO for sets
    private String url;
}
