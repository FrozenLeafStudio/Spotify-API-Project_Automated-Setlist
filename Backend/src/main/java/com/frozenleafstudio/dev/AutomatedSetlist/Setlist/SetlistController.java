package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/setlists")
public class SetlistController {
    private final SetlistService setlistService;

    @Autowired
    public SetlistController(SetlistService setlistService) {
        this.setlistService = setlistService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Setlist>> searchAndProcessArtistSetlists(@RequestParam String artistMbid, @RequestParam(defaultValue = "1") int pageNumber) {
        List<Setlist> setlistSearchResult = setlistService.fetchAndProcessArtistSetlists(artistMbid, pageNumber);
        if (setlistSearchResult.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(setlistSearchResult, HttpStatus.OK);
        }
    }
}
