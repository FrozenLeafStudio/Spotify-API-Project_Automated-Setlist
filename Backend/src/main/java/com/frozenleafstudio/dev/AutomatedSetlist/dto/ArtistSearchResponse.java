package com.frozenleafstudio.dev.AutomatedSetlist.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistSearchResponse {
    private List<ArtistSearchResult> artistList; // assuming this is the structure of the response

}
