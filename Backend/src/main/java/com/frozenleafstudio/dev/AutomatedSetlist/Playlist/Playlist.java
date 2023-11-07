package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;
import lombok.*;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "playlists")
public class Playlist {

    @Id
    private ObjectId id;
    private String playlist_id;
    private String name; // Name of the playlist
    private List<Track> tracks; // List of tracks in the playlist
    private String imageUrl; // URL of the playlist's cover image
    private String spotifyUrl; // URL to access the playlist on Spotify
}

