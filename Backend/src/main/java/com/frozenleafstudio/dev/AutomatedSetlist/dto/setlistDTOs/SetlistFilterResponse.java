package com.frozenleafstudio.dev.AutomatedSetlist.DTO.setlistDTOs;

import java.util.List;

import lombok.Data;

@Data
public class SetlistFilterResponse {
    private String type;
    private int itemsPerPage;
    private int page;
    private int total;
    private List<SetlistDTO> setlist; // List of setlists, mapped to SetlistDTO
}
