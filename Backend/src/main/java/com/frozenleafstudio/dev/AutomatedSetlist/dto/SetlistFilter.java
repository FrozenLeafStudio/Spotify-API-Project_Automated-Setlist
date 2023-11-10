package com.frozenleafstudio.dev.AutomatedSetlist.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import lombok.Data;

@Data
public class SetlistFilter {
    private String id;
    private String eventDate; // e.g., "2023-01-01"

    // Convert eventDate to LocalDate for comparison
    public LocalDate getEventLocalDate() {
        //ran into bug with datetime format - the value returned from the API is dd-mm-yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {
            return LocalDate.parse(eventDate, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Failed to parse date: " + eventDate + "stacktrace: " + e);
            // Handle the error appropriately, perhaps return null or a default value
            return null;
        }
    }
}
