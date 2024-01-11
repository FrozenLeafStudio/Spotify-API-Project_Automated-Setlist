package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "spotify-tokens")
public class SpotifyToken {
    @Id
    private ObjectId id;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenExpiry;
}
