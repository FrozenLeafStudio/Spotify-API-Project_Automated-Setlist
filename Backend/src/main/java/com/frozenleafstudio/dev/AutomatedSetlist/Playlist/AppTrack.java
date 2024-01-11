package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppTrack {
    private boolean trackFound;
    private String songUri;
    private String songName;
    private String artistName;
    private String albumName;
    private String albumImageUrl;
    private String details;
    private boolean isTape;
    private boolean isCover;
}
