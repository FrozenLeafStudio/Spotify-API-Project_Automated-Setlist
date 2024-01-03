package com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    // Convert eventDate to LocalDate for comparison
    public LocalDate getEventLocalDate() {
        //ran into bug with datetime format - the value returned from the API is dd-mm-yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {
            return LocalDate.parse(eventDate, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Failed to parse date: " + eventDate + "stacktrace: " + e);
            return null;
        }
    }
}
