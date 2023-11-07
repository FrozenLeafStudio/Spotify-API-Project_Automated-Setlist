package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.frozenleafstudio.dev.AutomatedSetlist.Artist.Artist;

@Document(collection = "Setlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Setlist{
    @Id
    private ObjectId id;
    private String setlistID;
    @DocumentReference
    private Artist artistId;
    private String eventDate;
    private List<String> songs;
}