package com.moviebooking.service.impl;

import com.moviebooking.entity.Movie;
import com.moviebooking.repository.IMovieRepository;
import com.moviebooking.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements IMovieService {

    @Autowired
    private IMovieRepository movieRepository;

    @Override
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(Movie movie) {
        Optional<Movie> existingMovie = movieRepository.findById(movie.getMovieId());
        if (existingMovie.isPresent()) {
            return movieRepository.save(movie);
        } else {
            throw new RuntimeException("Movie not found with ID: " + movie.getMovieId());
        }
    }

    @Override
    public Movie removeMovie(int movieId) {
        Optional<Movie> movie = movieRepository.findById(movieId);
        if (movie.isPresent()) {
            movieRepository.deleteById(movieId);
            return movie.get();
        } else {
            throw new RuntimeException("Movie not found with ID: " + movieId);
        }
    }

    @Override
    public Movie viewMovie(int movieId) {
        return movieRepository.findById(movieId)
            .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));
    }

    @Override
    public List<Movie> viewMovieList() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> viewMovieList(int theatreId) {
        // This would need additional logic to filter by theatre
        // For now, returning all movies
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> viewMovieList(LocalDate date) {
        // This would need additional logic to filter by date
        // For now, returning all movies
        return movieRepository.findAll();
    }
}
