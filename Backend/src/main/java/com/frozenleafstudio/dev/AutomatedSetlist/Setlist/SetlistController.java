package com.frozenleafstudio.dev.AutomatedSetlist.Setlist;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/setlists")
public class SetlistController {
    @GetMapping
    public ResponseEntity<String> allSetlists(){
        return new ResponseEntity<String>("This will show all searched setlists", HttpStatus.OK);

    }
}
