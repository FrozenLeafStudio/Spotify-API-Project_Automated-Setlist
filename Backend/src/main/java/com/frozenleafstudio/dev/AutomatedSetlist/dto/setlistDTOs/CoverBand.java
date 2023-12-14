package com.frozenleafstudio.dev.AutomatedSetlist.dto.setlistDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverBand {
    private String name;
    private String mbid;
    private String sortName;
    private String url;
}
