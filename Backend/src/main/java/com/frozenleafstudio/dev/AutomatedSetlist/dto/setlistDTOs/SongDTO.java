package com.frozenleafstudio.dev.AutomatedSetlist.dto.setlistDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SongDTO {
    private String name;
    private String info;
    private Boolean tape;
    private CoverBand cover;
    private ArtistDTO with;
}
