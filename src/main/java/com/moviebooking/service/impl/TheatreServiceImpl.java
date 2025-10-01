package com.moviebooking.service.impl;

import com.moviebooking.entity.Theatre;
import com.moviebooking.repository.ITheatreRepository;
import com.moviebooking.service.ITheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TheatreServiceImpl implements ITheatreService {

    @Autowired
    private ITheatreRepository theatreRepository;

    @Override
    public Theatre addTheatre(Theatre theatre) {
        if (theatreRepository.existsByTheatreName(theatre.getTheatreName())) {
            throw new RuntimeException("Theatre with name " + theatre.getTheatreName() + " already exists");
        }
        return theatreRepository.save(theatre);
    }

    @Override
    public Theatre updateTheatre(Theatre theatre) {
        Optional<Theatre> existingTheatre = theatreRepository.findById(theatre.getTheatreId());
        if (existingTheatre.isPresent()) {
            return theatreRepository.save(theatre);
        } else {
            throw new RuntimeException("Theatre not found with ID: " + theatre.getTheatreId());
        }
    }

    @Override
    public Theatre removeTheatre(int theatreId) {
        Optional<Theatre> theatre = theatreRepository.findById(theatreId);
        if (theatre.isPresent()) {
            theatreRepository.deleteById(theatreId);
            return theatre.get();
        } else {
            throw new RuntimeException("Theatre not found with ID: " + theatreId);
        }
    }

    @Override
    public Theatre viewTheatre(int theatreId) {
        return theatreRepository.findById(theatreId)
            .orElseThrow(() -> new RuntimeException("Theatre not found with ID: " + theatreId));
    }

    @Override
    public List<Theatre> viewAllTheatres() {
        return theatreRepository.findAll();
    }

    @Override
    public List<Theatre> viewTheatresByCity(String city) {
        return theatreRepository.findByTheatreCity(city);
    }
}
