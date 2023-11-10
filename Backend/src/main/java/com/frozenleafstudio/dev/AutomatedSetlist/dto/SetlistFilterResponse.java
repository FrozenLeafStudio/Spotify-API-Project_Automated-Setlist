package com.frozenleafstudio.dev.AutomatedSetlist.dto;

import java.util.List;

import lombok.Data;

@Data
public class SetlistFilterResponse {
    private int total;
    private List<SetlistFilter> setlist;
}
