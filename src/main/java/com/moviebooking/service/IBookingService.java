package com.moviebooking.service;

import java.time.LocalDate;
import java.util.List;

import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.MovieBookingSummary;
import com.moviebooking.dto.TicketView;
import com.moviebooking.entity.TicketBooking;

public interface IBookingService {

    TicketBooking addBooking(BookingRequest request);

    TicketBooking updateBooking(TicketBooking booking);

    TicketBooking cancelBooking(TicketBooking booking);

    List<TicketBooking> showAllBookings();

    List<TicketBooking> showAllBooking(int movieId);

    List<TicketBooking> showAllBooking(LocalDate date);

    List<TicketBooking> showBookingList(int showId);

    List<TicketView> findBookingsForCustomer(int customerId);

    double calculateTotalCost(int bookingId);

    List<MovieBookingSummary> summarizeBookingsByMovie();
}
