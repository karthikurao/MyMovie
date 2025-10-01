package com.moviebooking.controller;

import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.MovieBookingSummary;
import com.moviebooking.entity.TicketBooking;
import com.moviebooking.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private IBookingService bookingService;

    @PostMapping
    public ResponseEntity<?> addBooking(@RequestBody BookingRequest bookingRequest) {
        try {
            TicketBooking newBooking = bookingService.addBooking(bookingRequest);
            return new ResponseEntity<>(newBooking, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", "Unable to create booking"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<TicketBooking>> showAllBookings() {
        try {
            List<TicketBooking> bookings = bookingService.showAllBookings();
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/summary/movies")
    public ResponseEntity<List<MovieBookingSummary>> summarizeBookingsByMovie() {
        try {
            List<MovieBookingSummary> summaries = bookingService.summarizeBookingsByMovie();
            return new ResponseEntity<>(summaries, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketBooking> updateBooking(@PathVariable int id, @RequestBody TicketBooking booking) {
        try {
            booking.setBookingId(id);
            TicketBooking updatedBooking = bookingService.updateBooking(booking);
            return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TicketBooking> cancelBooking(@PathVariable int id) {
        try {
            TicketBooking booking = new TicketBooking();
            booking.setBookingId(id);
            TicketBooking cancelledBooking = bookingService.cancelBooking(booking);
            return new ResponseEntity<>(cancelledBooking, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<TicketBooking>> showAllBookingByMovie(@PathVariable int movieId) {
        try {
            List<TicketBooking> bookings = bookingService.showAllBooking(movieId);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<TicketBooking>> showAllBookingByDate(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<TicketBooking> bookings = bookingService.showAllBooking(localDate);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/show/{showId}")
    public ResponseEntity<List<TicketBooking>> showBookingListByShow(@PathVariable int showId) {
        try {
            List<TicketBooking> bookings = bookingService.showBookingList(showId);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/cost")
    public ResponseEntity<Double> calculateTotalCost(@PathVariable int id) {
        try {
            double totalCost = bookingService.calculateTotalCost(id);
            return new ResponseEntity<>(totalCost, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
