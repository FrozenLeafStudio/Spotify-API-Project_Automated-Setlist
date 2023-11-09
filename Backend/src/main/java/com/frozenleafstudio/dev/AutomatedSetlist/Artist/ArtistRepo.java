package com.frozenleafstudio.dev.AutomatedSetlist.Artist;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepo extends MongoRepository<Artist, ObjectId> {
    // Define custom query methods here if needed
    //Optional<Artist> findArtistByMbid(String mbid);

    Optional<Artist> findArtistByName(String id);
}