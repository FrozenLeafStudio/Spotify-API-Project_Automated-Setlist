package com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourDTO {
    private String name;
}
