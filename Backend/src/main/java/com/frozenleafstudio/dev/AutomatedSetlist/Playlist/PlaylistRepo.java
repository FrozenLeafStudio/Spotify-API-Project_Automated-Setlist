package com.frozenleafstudio.dev.automatedSetlist.playlist;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepo extends MongoRepository<Playlist, ObjectId> {

    Playlist getBysetlistID(String setlistId);

}
