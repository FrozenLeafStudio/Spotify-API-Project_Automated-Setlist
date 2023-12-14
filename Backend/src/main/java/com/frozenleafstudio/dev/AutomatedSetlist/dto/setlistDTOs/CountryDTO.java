package com.frozenleafstudio.dev.AutomatedSetlist.dto.setlistDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryDTO {
    private String code;
    private String name;
}
