package com.frozenleafstudio.dev.automatedSetlist.dto.setlistDTOs;

import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data  
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetsDTO {
    private List<SetDTO> set; // List of sets
}
