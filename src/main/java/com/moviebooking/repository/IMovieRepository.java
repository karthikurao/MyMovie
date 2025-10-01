package com.moviebooking.repository;

import com.moviebooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IMovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByMovieNameContainingIgnoreCase(String movieName);
    List<Movie> findByMovieGenre(String genre);
    List<Movie> findByLanguage(String language);
}
