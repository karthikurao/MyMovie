package com.moviebooking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketView {

    private final int bookingId;
    private final Integer ticketId;
    private final LocalDate bookingDate;
    private final Integer bookingReference;
    private final Integer transactionId;
    private final String transactionMode;
    private final String transactionStatus;
    private final String paymentReference;
    private final double totalCost;
    private final List<String> seatNumbers;
    private final int seatCount;

    private final Integer showId;
    private final String showName;
    private final LocalDateTime showStartTime;
    private final LocalDateTime showEndTime;

    private final Integer movieId;
    private final String movieName;
    private final String movieGenre;
    private final String language;
    private final String movieImageUrl;

    private final Integer theatreId;
    private final String theatreName;
    private final String theatreCity;

    private final Integer screenId;
    private final String screenName;

    public TicketView(
            int bookingId,
            Integer ticketId,
            LocalDate bookingDate,
            Integer bookingReference,
            Integer transactionId,
            String transactionMode,
            String transactionStatus,
            String paymentReference,
            double totalCost,
            List<String> seatNumbers,
            int seatCount,
            Integer showId,
            String showName,
            LocalDateTime showStartTime,
            LocalDateTime showEndTime,
            Integer movieId,
            String movieName,
            String movieGenre,
            String language,
            String movieImageUrl,
            Integer theatreId,
            String theatreName,
            String theatreCity,
            Integer screenId,
            String screenName
    ) {
        this.bookingId = bookingId;
        this.ticketId = ticketId;
        this.bookingDate = bookingDate;
        this.bookingReference = bookingReference;
        this.transactionId = transactionId;
        this.transactionMode = transactionMode;
        this.transactionStatus = transactionStatus;
        this.paymentReference = paymentReference;
        this.totalCost = totalCost;
        this.seatNumbers = seatNumbers;
        this.seatCount = seatCount;
        this.showId = showId;
        this.showName = showName;
        this.showStartTime = showStartTime;
        this.showEndTime = showEndTime;
        this.movieId = movieId;
        this.movieName = movieName;
        this.movieGenre = movieGenre;
        this.language = language;
        this.movieImageUrl = movieImageUrl;
        this.theatreId = theatreId;
        this.theatreName = theatreName;
        this.theatreCity = theatreCity;
        this.screenId = screenId;
        this.screenName = screenName;
    }

    public int getBookingId() {
        return bookingId;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public Integer getBookingReference() {
        return bookingReference;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public Integer getShowId() {
        return showId;
    }

    public String getShowName() {
        return showName;
    }

    public LocalDateTime getShowStartTime() {
        return showStartTime;
    }

    public LocalDateTime getShowEndTime() {
        return showEndTime;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getMovieGenre() {
        return movieGenre;
    }

    public String getLanguage() {
        return language;
    }

    public String getMovieImageUrl() {
        return movieImageUrl;
    }

    public Integer getTheatreId() {
        return theatreId;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public String getTheatreCity() {
        return theatreCity;
    }

    public Integer getScreenId() {
        return screenId;
    }

    public String getScreenName() {
        return screenName;
    }
}
