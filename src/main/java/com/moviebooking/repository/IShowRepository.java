package com.moviebooking.repository;

import com.moviebooking.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IShowRepository extends JpaRepository<Show, Integer> {
    List<Show> findByTheatreId(int theatreId);
    List<Show> findByScreenId(int screenId);

    @Query("SELECT s FROM Show s WHERE DATE(s.showStartTime) = :date")
    List<Show> findByShowDate(@Param("date") LocalDate date);

    @Query("SELECT s FROM Show s WHERE s.movie.movieId = :movieId")
    List<Show> findByMovieId(@Param("movieId") int movieId);
}
