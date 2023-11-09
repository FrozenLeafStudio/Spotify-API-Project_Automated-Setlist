package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.frozenleafstudio.dev.AutomatedSetlist.Artist.Artist;

@Document(collection = "Setlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Setlist{
    @Id
    private ObjectId id;
    private String setlistID;
    private Artist artistId;
    private String eventDate;
    private List<String> songs;
}