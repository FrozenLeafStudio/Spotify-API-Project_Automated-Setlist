package com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityDTO {
    private String name;
    private String state;
    private String stateCode;
    private CountryDTO country;
}
