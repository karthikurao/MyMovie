package com.moviebooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int showId;

    @NotNull(message = "Show start time is required")
    @Column(nullable = false)
    private LocalDateTime showStartTime;

    @NotNull(message = "Show end time is required")
    @Column(nullable = false)
    private LocalDateTime showEndTime;

    @NotBlank(message = "Show name is required")
    @Column(nullable = false)
    private String showName;

    @NotNull(message = "Screen ID is required")
    @Column(nullable = false)
    private int screenId;

    @NotNull(message = "Theatre ID is required")
    @Column(nullable = false)
    private int theatreId;

    @Column(name = "movieId")
    private Integer movieId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movieId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"shows", "hibernateLazyInitializer", "handler"})
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screenId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"shows", "hibernateLazyInitializer", "handler"})
    private Screen screen;

    // Constructors
    public Show() {}

    public Show(LocalDateTime showStartTime, LocalDateTime showEndTime, String showName, int screenId, int theatreId) {
        this.showStartTime = showStartTime;
        this.showEndTime = showEndTime;
        this.showName = showName;
        this.screenId = screenId;
        this.theatreId = theatreId;
    }

    // Getters and Setters
    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public LocalDateTime getShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(LocalDateTime showStartTime) {
        this.showStartTime = showStartTime;
    }

    public LocalDateTime getShowEndTime() {
        return showEndTime;
    }

    public void setShowEndTime(LocalDateTime showEndTime) {
        this.showEndTime = showEndTime;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getScreenId() {
        return screenId;
    }

    public void setScreenId(int screenId) {
        this.screenId = screenId;
    }

    public int getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(int theatreId) {
        this.theatreId = theatreId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }
}
