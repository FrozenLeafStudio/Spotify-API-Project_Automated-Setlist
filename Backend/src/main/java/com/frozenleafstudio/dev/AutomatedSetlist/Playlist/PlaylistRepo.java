package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepo extends MongoRepository<Playlist, ObjectId> {

    Playlist getBysetlistID(String setlistId);

}
