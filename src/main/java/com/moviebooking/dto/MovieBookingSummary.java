package com.moviebooking.dto;

public class MovieBookingSummary {

    private final int movieId;
    private final String movieName;
    private final long totalBookings;
    private final long totalSeats;
    private final double totalRevenue;

    public MovieBookingSummary(int movieId, String movieName, long totalBookings, long totalSeats, double totalRevenue) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.totalBookings = totalBookings;
        this.totalSeats = totalSeats;
        this.totalRevenue = totalRevenue;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public long getTotalSeats() {
        return totalSeats;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
