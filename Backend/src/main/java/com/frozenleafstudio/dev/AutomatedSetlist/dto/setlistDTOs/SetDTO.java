package com.frozenleafstudio.dev.AutomatedSetlist.dto.setlistDTOs;

import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetDTO {
    private List<SongDTO> song; // List of songs
}
