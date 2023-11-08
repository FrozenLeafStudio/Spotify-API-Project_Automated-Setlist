package com.frozenleafstudio.dev.AutomatedSetlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistSearchResult {
    private String mbid;
    private String name;
}
