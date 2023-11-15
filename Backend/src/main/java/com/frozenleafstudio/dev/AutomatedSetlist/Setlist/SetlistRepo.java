package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetlistRepo extends MongoRepository<Setlist, ObjectId> {

    List<Setlist> findSetlistByMbid(String mbid);

    Setlist findSetlistBySetlistID(String setlistID);
}
