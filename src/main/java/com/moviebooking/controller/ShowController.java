package com.moviebooking.controller;

import com.moviebooking.entity.Show;
import com.moviebooking.service.IShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
@CrossOrigin(origins = "*")
public class ShowController {

    @Autowired
    private IShowService showService;

    @PostMapping
    public ResponseEntity<Show> addShow(@RequestBody Show show) {
        try {
            Show newShow = showService.addShow(show);
            return new ResponseEntity<>(newShow, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Show> updateShow(@PathVariable int id, @RequestBody Show show) {
        try {
            show.setShowId(id);
            Show updatedShow = showService.updateShow(show);
            return new ResponseEntity<>(updatedShow, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Show> removeShow(@PathVariable int id) {
        try {
            Show show = new Show();
            show.setShowId(id);
            Show removedShow = showService.removeShow(show);
            return new ResponseEntity<>(removedShow, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Show> viewShow(@PathVariable int id) {
        try {
            Show show = new Show();
            show.setShowId(id);
            Show foundShow = showService.viewShow(show);
            return new ResponseEntity<>(foundShow, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Show>> viewAllShows() {
        try {
            List<Show> shows = showService.viewAllShows();
            return new ResponseEntity<>(shows, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<List<Show>> viewShowsByTheatre(@PathVariable int theatreId) {
        try {
            List<Show> shows = showService.viewShowList(theatreId);
            return new ResponseEntity<>(shows, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Show>> viewShowsByDate(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<Show> shows = showService.viewShowList(localDate);
            return new ResponseEntity<>(shows, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
