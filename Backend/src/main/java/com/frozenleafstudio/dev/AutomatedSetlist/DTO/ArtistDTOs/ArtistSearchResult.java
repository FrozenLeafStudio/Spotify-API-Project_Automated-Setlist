package com.frozenleafstudio.dev.AutomatedSetlist.DTO.ArtistDTOs;

import lombok.Data;

@Data
public class ArtistSearchResult {
    private String mbid;
    private String tmid;
    private String name;
    private String sortName;
    private String disambiguation;
    private String url;
}
