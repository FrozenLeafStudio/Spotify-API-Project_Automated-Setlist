package com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs;

import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetDTO {
    private String name;
    private List<SongDTO> song;}
