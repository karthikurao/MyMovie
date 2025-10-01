package com.moviebooking.repository;

import com.moviebooking.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IScreenRepository extends JpaRepository<Screen, Integer> {
    List<Screen> findByTheatreId(int theatreId);
    List<Screen> findByScreenName(String screenName);
}
