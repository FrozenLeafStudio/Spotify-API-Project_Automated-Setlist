package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.ArtistDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.SetsDTO;
import com.frozenleafstudio.dev.AutomatedSetlist.DTO.SetlistDTOs.VenueDTO;

@Document(collection = "Setlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Setlist{
    @Id
    private ObjectId id; //generated for mongodb
    @Indexed(unique = true)
    private String setlistID; //setlistID from setlist.fm api
    private String eventDate; //event date from setlist.fm api
    private ArtistDTO artist;
    private VenueDTO venue;
    private String tourName; //Same as venue, tour is an object from setlist -> to string.
    private SetsDTO sets; //list of songs from a given set
    private String url;
}