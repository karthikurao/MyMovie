package com.moviebooking.controller;

import com.moviebooking.entity.Movie;
import com.moviebooking.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    @Autowired
    private IMovieService movieService;

    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        try {
            Movie newMovie = movieService.addMovie(movie);
            return new ResponseEntity<>(newMovie, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable int id, @RequestBody Movie movie) {
        try {
            movie.setMovieId(id);
            Movie updatedMovie = movieService.updateMovie(movie);
            return new ResponseEntity<>(updatedMovie, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Movie> removeMovie(@PathVariable int id) {
        try {
            Movie removedMovie = movieService.removeMovie(id);
            return new ResponseEntity<>(removedMovie, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> viewMovie(@PathVariable int id) {
        try {
            Movie movie = movieService.viewMovie(id);
            return new ResponseEntity<>(movie, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Movie>> viewAllMovies() {
        try {
            List<Movie> movies = movieService.viewMovieList();
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<List<Movie>> viewMoviesByTheatre(@PathVariable int theatreId) {
        try {
            List<Movie> movies = movieService.viewMovieList(theatreId);
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Movie>> viewMoviesByDate(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<Movie> movies = movieService.viewMovieList(localDate);
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
