package com.frozenleafstudio.dev.AutomatedSetlist.DTO.ArtistDTOs;

import lombok.Data;
import java.util.List;

@Data
public class ArtistSearchResponse {
    private String type;
    private int itemsPerPage;
    private int page;
    private int total;
    private List<ArtistSearchResult> artist;
}

