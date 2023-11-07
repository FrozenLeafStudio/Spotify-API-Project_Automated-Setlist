package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetlistRepo extends MongoRepository<Setlist, ObjectId> {
    // Define custom query methods here if needed
}
