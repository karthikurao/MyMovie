package com.moviebooking.service.impl;

import com.moviebooking.entity.Screen;
import com.moviebooking.repository.IScreenRepository;
import com.moviebooking.service.IScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ScreenServiceImpl implements IScreenService {

    @Autowired
    private IScreenRepository screenRepository;

    @Override
    public Screen addScreen(Screen screen) {
        return screenRepository.save(screen);
    }

    @Override
    public Screen updateScreen(Screen screen) {
        Optional<Screen> existingScreen = screenRepository.findById(screen.getScreenId());
        if (existingScreen.isPresent()) {
            return screenRepository.save(screen);
        } else {
            throw new RuntimeException("Screen not found with ID: " + screen.getScreenId());
        }
    }

    @Override
    public Screen removeScreen(int screenId) {
        Optional<Screen> screen = screenRepository.findById(screenId);
        if (screen.isPresent()) {
            screenRepository.deleteById(screenId);
            return screen.get();
        } else {
            throw new RuntimeException("Screen not found with ID: " + screenId);
        }
    }

    @Override
    public Screen viewScreen(int screenId) {
        return screenRepository.findById(screenId)
            .orElseThrow(() -> new RuntimeException("Screen not found with ID: " + screenId));
    }

    @Override
    public List<Screen> viewAllScreens() {
        return screenRepository.findAll();
    }

    @Override
    public List<Screen> viewScreensByTheatre(int theatreId) {
        return screenRepository.findByTheatreId(theatreId);
    }

    @Override
    public List<Screen> viewScreensByName(String screenName) {
        return screenRepository.findByScreenName(screenName);
    }
}
