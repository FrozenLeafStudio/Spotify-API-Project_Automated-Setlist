package com.frozenleafstudio.dev.automatedSetlist.dto.setlistDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistDTO {
    private String mbid;
    private String name;
    private String url;
}
