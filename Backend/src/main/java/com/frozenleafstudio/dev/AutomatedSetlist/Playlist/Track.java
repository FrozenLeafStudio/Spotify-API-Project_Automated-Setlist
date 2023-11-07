package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Track {
    private String songName;
    private String artistName;
    private String albumName;
    private String albumImageUrl;

    // No need to write getters and setters manually with Lombok

    // Additional methods as needed
}
