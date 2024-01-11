package com.frozenleafstudio.dev.automatedSetlist.setlist;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SetlistRepo extends MongoRepository<Setlist, ObjectId> {

    @Query("{'artist.mbid': ?0}")
    List<Setlist> findSetlistsByArtistMbid(String mbid);

    Setlist findSetlistBySetlistID(String setlistID);

}
