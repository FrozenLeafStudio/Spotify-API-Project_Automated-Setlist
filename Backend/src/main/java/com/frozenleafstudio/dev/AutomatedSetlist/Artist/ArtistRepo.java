package com.frozenleafstudio.dev.AutomatedSetlist.Artist;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepo extends MongoRepository<Artist, ObjectId> {

    default Optional<Artist> findArtistByName(String name) {
        // Normalize the input name to lowercase
        String normalizedArtistName = name.toLowerCase();
        
        // Query for the artist using the normalized name
        return this.findByName(normalizedArtistName);
    }
    Optional<Artist> findByNameAndMbid(String name, String mbid);
    Optional<Artist> findByName(String name);

    Optional<Artist> findByMbid(String artistMbid);
}