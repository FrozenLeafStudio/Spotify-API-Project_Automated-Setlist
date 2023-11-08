package com.frozenleafstudio.dev.AutomatedSetlist.Artist;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.frozenleafstudio.dev.AutomatedSetlist.dto.ArtistSearchResult;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController { 
    @Autowired
    private ArtistService artistService;
    
    @GetMapping
    public ResponseEntity<List<Artist>> getAllArtists(){
        return new ResponseEntity<List<Artist>>(artistService.AllArtists(), HttpStatus.OK);

    }
    @GetMapping("/{mbid}")
    public ResponseEntity<Optional<Artist>> getSingleArtist(@PathVariable String mbid){
        System.out.println(mbid);
        return new ResponseEntity<Optional<Artist>>(artistService.singleArtist(mbid), HttpStatus.OK);
    }
    //prototyping DTOs; change return object to ResponseEntity<ArtistSearchResult> after confirming raw JSON response
    @GetMapping("/search")
    public ResponseEntity<?> searchArtist(@RequestParam String artistName) {
        System.out.println("from searchArtist method: " + artistName);
        String rawApiResponse = artistService.searchArtistOnSetlist(artistName);
        if(rawApiResponse.startsWith("Error")){
            return new ResponseEntity<>(rawApiResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(rawApiResponse);
        /* Optional<ArtistSearchResult> artistSearchResult = artistService.searchArtistOnSetlist(artistName);
        return artistSearchResult
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); */
    }
}
