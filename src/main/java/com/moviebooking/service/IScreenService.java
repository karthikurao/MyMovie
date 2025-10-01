package com.moviebooking.service;

import com.moviebooking.entity.Screen;
import java.util.List;

public interface IScreenService {
    Screen addScreen(Screen screen);
    Screen updateScreen(Screen screen);
    Screen removeScreen(int screenId);
    Screen viewScreen(int screenId);
    List<Screen> viewAllScreens();
    List<Screen> viewScreensByTheatre(int theatreId);
    List<Screen> viewScreensByName(String screenName);
}
