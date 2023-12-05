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

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController { 
    @Autowired
    private ArtistService artistService;
    
    @GetMapping
    public ResponseEntity<List<Artist>> getAllArtists(){
        return new ResponseEntity<List<Artist>>(artistService.allArtists(), HttpStatus.OK);

    }
    @GetMapping("/{mbid}")
    public ResponseEntity<Optional<Artist>> getSingleArtist(@PathVariable String mbid){
        System.out.println(mbid);
        return new ResponseEntity<Optional<Artist>>(artistService.singleArtist(mbid), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Artist> searchArtist(@RequestParam String artistName) {
        Optional<Artist> artistSearchResult = artistService.searchArtistOnSetlist(artistName);
        return artistSearchResult
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
