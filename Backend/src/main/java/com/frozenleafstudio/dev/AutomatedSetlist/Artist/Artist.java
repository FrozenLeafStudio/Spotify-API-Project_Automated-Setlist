package com.frozenleafstudio.dev.AutomatedSetlist.Artist;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.frozenleafstudio.dev.AutomatedSetlist.Setlist.Setlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "Artists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Artist {
    @Id
    private ObjectId id;
    private String mbid; // Musicbrainz Identifier
    private String tmid; // Ticket Master Identifier
    private String name;
    private String sortName;
    private String disambiguation;
    private String url;
    @DocumentReference
    private Setlist setlists;
}
