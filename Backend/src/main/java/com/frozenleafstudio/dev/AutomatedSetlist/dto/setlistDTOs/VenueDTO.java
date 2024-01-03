package com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VenueDTO {
    private String name;
    private CityDTO city;
    private String url;
}
