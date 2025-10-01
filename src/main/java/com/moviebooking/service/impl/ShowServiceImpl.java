package com.moviebooking.service.impl;

import com.moviebooking.entity.Show;
import com.moviebooking.repository.IShowRepository;
import com.moviebooking.service.IShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ShowServiceImpl implements IShowService {

    @Autowired
    private IShowRepository showRepository;

    @Override
    public Show addShow(Show show) {
        return showRepository.save(show);
    }

    @Override
    public Show updateShow(Show show) {
        Optional<Show> existingShow = showRepository.findById(show.getShowId());
        if (existingShow.isPresent()) {
            return showRepository.save(show);
        } else {
            throw new RuntimeException("Show not found with ID: " + show.getShowId());
        }
    }

    @Override
    public Show removeShow(Show show) {
        Optional<Show> existingShow = showRepository.findById(show.getShowId());
        if (existingShow.isPresent()) {
            showRepository.delete(show);
            return show;
        } else {
            throw new RuntimeException("Show not found with ID: " + show.getShowId());
        }
    }

    @Override
    public Show viewShow(Show show) {
        return showRepository.findById(show.getShowId())
            .orElseThrow(() -> new RuntimeException("Show not found with ID: " + show.getShowId()));
    }

    @Override
    public List<Show> viewShowList(int theatreId) {
        return showRepository.findByTheatreId(theatreId);
    }

    @Override
    public List<Show> viewShowList(LocalDate date) {
        return showRepository.findByShowDate(date);
    }

    @Override
    public List<Show> viewAllShows() {
        return showRepository.findAll();
    }
}
