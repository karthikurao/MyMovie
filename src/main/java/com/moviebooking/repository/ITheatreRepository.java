package com.moviebooking.repository;

import com.moviebooking.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ITheatreRepository extends JpaRepository<Theatre, Integer> {
    List<Theatre> findByTheatreCity(String city);
    List<Theatre> findByTheatreNameContainingIgnoreCase(String name);
    boolean existsByTheatreName(String theatreName);
}
