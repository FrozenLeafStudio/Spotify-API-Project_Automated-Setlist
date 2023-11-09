package com.frozenleafstudio.dev.AutomatedSetlist.dto;

import lombok.Data;

@Data
public class ArtistSearchResult {
    private String mbid;
    private String tmid; // This can be null if the API does not always return it
    private String name;
    private String sortName;
    private String disambiguation;
    private String url;
}
