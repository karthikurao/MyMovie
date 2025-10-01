package com.moviebooking.service;

import com.moviebooking.entity.Show;
import java.time.LocalDate;
import java.util.List;

public interface IShowService {
    Show addShow(Show show);
    Show updateShow(Show show);
    Show removeShow(Show show);
    Show viewShow(Show show);
    List<Show> viewShowList(int theatreId);
    List<Show> viewShowList(LocalDate date);
    List<Show> viewAllShows();
}
