package com.moviebooking.service;

import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.MovieBookingSummary;
import com.moviebooking.entity.TicketBooking;
import java.time.LocalDate;
import java.util.List;

public interface IBookingService {
    TicketBooking addBooking(BookingRequest request);
    TicketBooking updateBooking(TicketBooking booking);
    TicketBooking cancelBooking(TicketBooking booking);
    List<TicketBooking> showAllBookings();
    List<TicketBooking> showAllBooking(int movieId);
    List<TicketBooking> showAllBooking(LocalDate date);
    List<TicketBooking> showBookingList(int showId);
    double calculateTotalCost(int bookingId);
    List<MovieBookingSummary> summarizeBookingsByMovie();
}
