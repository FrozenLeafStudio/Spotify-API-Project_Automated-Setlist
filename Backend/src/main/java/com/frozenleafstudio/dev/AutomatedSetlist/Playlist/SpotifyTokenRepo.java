package com.frozenleafstudio.dev.automatedSetlist.playlist;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotifyTokenRepo extends MongoRepository<SpotifyToken, ObjectId>{
    
    default SpotifyToken getSpotifyToken() {
        return findAll().stream().findFirst().orElse(null);
    }

    void deleteAll();
}
