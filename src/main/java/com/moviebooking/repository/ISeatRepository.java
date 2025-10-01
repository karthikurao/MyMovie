package com.moviebooking.repository;

import com.moviebooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ISeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByType(String type);
    List<Seat> findByPriceBetween(double minPrice, double maxPrice);
    boolean existsBySeatNumber(String seatNumber);
}
