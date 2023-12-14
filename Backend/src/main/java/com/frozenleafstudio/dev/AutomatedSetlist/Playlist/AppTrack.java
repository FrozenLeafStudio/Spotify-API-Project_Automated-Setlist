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
    private String details;      // Additional information about the track
    private boolean isTape;      // Indicates if the track is a tape
    private boolean isCover;
}
