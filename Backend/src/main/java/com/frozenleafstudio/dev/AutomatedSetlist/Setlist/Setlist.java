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
    private ObjectId id; //generated for mongodb
    private String setlistID; //setlistID from setlist.fm api
    private String eventDate; //event date from setlist.fm api
    private String mbid; //this would match to an artist found in the Artists Collection in mongoDB
    private String venueName; //Venue is an object in setlist FM, rather than store the object I will parse the name and location then store them as strings
    private String venueLocation;
    private String tourName; //Same as venue, tour is an object from setlist -> to string.
    private String url;
    private List<String> songs; //list of songs from a given set
}