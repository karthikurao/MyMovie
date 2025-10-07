package com.moviebooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "movies")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int movieId;

    @NotBlank(message = "Movie name is required")
    @Column(nullable = false)
    private String movieName;

    @NotBlank(message = "Movie genre is required")
    @Column(nullable = false)
    private String movieGenre;

    @NotBlank(message = "Movie hours is required")
    @Column(nullable = false)
    private String movieHours;

    @NotBlank(message = "Language is required")
    @Column(nullable = false)
    private String language;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"movie", "screen", "hibernateLazyInitializer", "handler"})
    private List<Show> shows;

    // Constructors
    public Movie() {}

    public Movie(String movieName, String movieGenre, String movieHours, String language, String description) {
        this.movieName = movieName;
        this.movieGenre = movieGenre;
        this.movieHours = movieHours;
        this.language = language;
        this.description = description;
    }

    public Movie(String movieName, String movieGenre, String movieHours, String language, String description, String imageUrl) {
        this.movieName = movieName;
        this.movieGenre = movieGenre;
        this.movieHours = movieHours;
        this.language = language;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieGenre() {
        return movieGenre;
    }

    public void setMovieGenre(String movieGenre) {
        this.movieGenre = movieGenre;
    }

    public String getMovieHours() {
        return movieHours;
    }

    public void setMovieHours(String movieHours) {
        this.movieHours = movieHours;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }
}
