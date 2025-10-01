package com.moviebooking.service.impl;

import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.MovieBookingSummary;
import com.moviebooking.entity.Customer;
import com.moviebooking.entity.Show;
import com.moviebooking.entity.Ticket;
import com.moviebooking.entity.TicketBooking;
import com.moviebooking.repository.IBookingRepository;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.repository.IShowRepository;
import com.moviebooking.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements IBookingService {

    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IShowRepository showRepository;

    @Override
    public TicketBooking addBooking(BookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Booking request must not be null");
        }

        if (request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected");
        }

        List<String> normalizedSeats = request.getSeatNumbers().stream()
            .filter(seat -> seat != null && !seat.isBlank())
            .map(seat -> seat.trim().toUpperCase())
            .collect(Collectors.toList());

        if (normalizedSeats.isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected");
        }

        Set<String> uniqueSeats = new LinkedHashSet<>(normalizedSeats);
        if (uniqueSeats.size() != normalizedSeats.size()) {
            throw new IllegalArgumentException("Duplicate seats selected. Please review your selection.");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + request.getCustomerId()));

        Show show = showRepository.findById(request.getShowId())
            .orElseThrow(() -> new IllegalArgumentException("Show not found with ID: " + request.getShowId()));

        List<String> reservedSeats = bookingRepository.findReservedSeatNumbersByShow(show.getShowId())
            .stream()
            .filter(seat -> seat != null && !seat.isBlank())
            .map(seat -> seat.trim().toUpperCase())
            .collect(Collectors.toList());

        Set<String> reservedSeatSet = new HashSet<>(reservedSeats);
        Set<String> requestedSeatSet = new HashSet<>(uniqueSeats);
        requestedSeatSet.retainAll(reservedSeatSet);

        if (!requestedSeatSet.isEmpty()) {
            List<String> unavailableSeats = requestedSeatSet.stream()
                .sorted()
                .toList();
            throw new IllegalArgumentException("Selected seats are no longer available: " + unavailableSeats);
        }

        if (request.getTotalCost() <= 0) {
            throw new IllegalArgumentException("Total cost must be greater than zero");
        }

        Ticket ticket = new Ticket();
        ticket.setNoOfSeats(uniqueSeats.size());
        ticket.setSeatNumber(new ArrayList<>(uniqueSeats));
        ticket.setBookingRef(generateBookingReference());
        ticket.setTicketStatus(true);

        TicketBooking booking = new TicketBooking();
        booking.setShowId(show.getShowId());
        booking.setBookingDate(request.getBookingDate() != null ? request.getBookingDate() : LocalDate.now());
        booking.setTransactionId(generateTransactionId());
        booking.setTransactionMode(
            request.getPaymentMode() != null && !request.getPaymentMode().isBlank()
                ? request.getPaymentMode().toUpperCase()
                : "ONLINE"
        );
        booking.setTransactionStatus("CONFIRMED");
        booking.setTotalCost(request.getTotalCost());
        booking.setCustomer(customer);
        booking.setTicket(ticket);
        booking.setShow(show);
        ticket.setBooking(booking);

        try {
            return bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Unable to create booking with provided data", e);
        }
    }

    @Override
    public TicketBooking updateBooking(TicketBooking booking) {
        Optional<TicketBooking> existingBooking = bookingRepository.findById(booking.getBookingId());
        if (existingBooking.isPresent()) {
            return bookingRepository.save(booking);
        } else {
            throw new RuntimeException("Booking not found with ID: " + booking.getBookingId());
        }
    }

    @Override
    public TicketBooking cancelBooking(TicketBooking booking) {
        Optional<TicketBooking> existingBooking = bookingRepository.findById(booking.getBookingId());
        if (existingBooking.isPresent()) {
            booking.setTransactionStatus("CANCELLED");
            return bookingRepository.save(booking);
        } else {
            throw new RuntimeException("Booking not found with ID: " + booking.getBookingId());
        }
    }

    @Override
    public List<TicketBooking> showAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<TicketBooking> showAllBooking(int movieId) {
        return bookingRepository.findByShowMovieId(movieId);
    }

    @Override
    public List<TicketBooking> showAllBooking(LocalDate date) {
        return bookingRepository.findByBookingDate(date);
    }

    @Override
    public List<TicketBooking> showBookingList(int showId) {
        return bookingRepository.findByShowId(showId);
    }

    @Override
    public double calculateTotalCost(int bookingId) {
        return bookingRepository.calculateTotalCost(bookingId);
    }

    @Override
    public List<MovieBookingSummary> summarizeBookingsByMovie() {
        return bookingRepository.summarizeBookingsByMovie();
    }

    private int generateTransactionId() {
        return ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
    }

    private int generateBookingReference() {
        return ThreadLocalRandom.current().nextInt(1_000_000, 9_999_999);
    }
}
