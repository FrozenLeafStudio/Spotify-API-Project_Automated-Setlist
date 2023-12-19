package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/setlists")
public class SetlistController {
    @Autowired
    private SetlistService setlistService;

        @GetMapping("/search")
        public ResponseEntity<?> searchAndProcessArtistSetlists(@RequestParam String artistMbid, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        List<Setlist> setlistSearchResult = setlistService.searchAndProcessArtistSetlists(artistMbid, startDate);
        if(setlistSearchResult.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(setlistSearchResult, HttpStatus.OK);
        }
    }


}
