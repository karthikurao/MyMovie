package com.moviebooking.service;

import com.moviebooking.entity.Movie;
import java.time.LocalDate;
import java.util.List;

public interface IMovieService {
    Movie addMovie(Movie movie);
    Movie updateMovie(Movie movie);
    Movie removeMovie(int movieId);
    Movie viewMovie(int movieId);
    List<Movie> viewMovieList();
    List<Movie> viewMovieList(int theatreId);
    List<Movie> viewMovieList(LocalDate date);
}
