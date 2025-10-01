package com.moviebooking.service;

import com.moviebooking.entity.Theatre;
import java.util.List;

public interface ITheatreService {
    Theatre addTheatre(Theatre theatre);
    Theatre updateTheatre(Theatre theatre);
    Theatre removeTheatre(int theatreId);
    Theatre viewTheatre(int theatreId);
    List<Theatre> viewAllTheatres();
    List<Theatre> viewTheatresByCity(String city);
}
