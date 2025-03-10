package com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistDTO {
    private String mbid;
    private String name;
    private String url;
}
