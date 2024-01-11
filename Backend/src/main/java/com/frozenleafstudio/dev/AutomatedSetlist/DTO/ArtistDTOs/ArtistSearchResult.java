package com.frozenleafstudio.dev.automatedSetlist.dto.artistDTOs;

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
