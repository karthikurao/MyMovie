package com.moviebooking.controller;

import com.moviebooking.entity.Theatre;
import com.moviebooking.service.ITheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/theatres")
@CrossOrigin(origins = "*")
public class TheatreController {

    @Autowired
    private ITheatreService theatreService;

    @PostMapping
    public ResponseEntity<Theatre> addTheatre(@RequestBody Theatre theatre) {
        try {
            Theatre newTheatre = theatreService.addTheatre(theatre);
            return new ResponseEntity<>(newTheatre, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Theatre> updateTheatre(@PathVariable int id, @RequestBody Theatre theatre) {
        try {
            theatre.setTheatreId(id);
            Theatre updatedTheatre = theatreService.updateTheatre(theatre);
            return new ResponseEntity<>(updatedTheatre, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Theatre> removeTheatre(@PathVariable int id) {
        try {
            Theatre removedTheatre = theatreService.removeTheatre(id);
            return new ResponseEntity<>(removedTheatre, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theatre> viewTheatre(@PathVariable int id) {
        try {
            Theatre theatre = theatreService.viewTheatre(id);
            return new ResponseEntity<>(theatre, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Theatre>> viewAllTheatres() {
        try {
            List<Theatre> theatres = theatreService.viewAllTheatres();
            return new ResponseEntity<>(theatres, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Theatre>> viewTheatresByCity(@PathVariable String city) {
        try {
            List<Theatre> theatres = theatreService.viewTheatresByCity(city);
            return new ResponseEntity<>(theatres, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
