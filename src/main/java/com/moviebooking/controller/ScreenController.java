package com.moviebooking.controller;

import com.moviebooking.entity.Screen;
import com.moviebooking.service.IScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/screens")
@CrossOrigin(origins = "*")
public class ScreenController {

    @Autowired
    private IScreenService screenService;

    @PostMapping
    public ResponseEntity<Screen> addScreen(@RequestBody Screen screen) {
        try {
            Screen newScreen = screenService.addScreen(screen);
            return new ResponseEntity<>(newScreen, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Screen> updateScreen(@PathVariable int id, @RequestBody Screen screen) {
        try {
            screen.setScreenId(id);
            Screen updatedScreen = screenService.updateScreen(screen);
            return new ResponseEntity<>(updatedScreen, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Screen> removeScreen(@PathVariable int id) {
        try {
            Screen removedScreen = screenService.removeScreen(id);
            return new ResponseEntity<>(removedScreen, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Screen> viewScreen(@PathVariable int id) {
        try {
            Screen screen = screenService.viewScreen(id);
            return new ResponseEntity<>(screen, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Screen>> viewAllScreens() {
        try {
            List<Screen> screens = screenService.viewAllScreens();
            return new ResponseEntity<>(screens, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<List<Screen>> viewScreensByTheatre(@PathVariable int theatreId) {
        try {
            List<Screen> screens = screenService.viewScreensByTheatre(theatreId);
            return new ResponseEntity<>(screens, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/name/{screenName}")
    public ResponseEntity<List<Screen>> viewScreensByName(@PathVariable String screenName) {
        try {
            List<Screen> screens = screenService.viewScreensByName(screenName);
            return new ResponseEntity<>(screens, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
