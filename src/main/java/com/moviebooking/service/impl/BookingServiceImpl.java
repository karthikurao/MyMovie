package com.moviebooking.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.MovieBookingSummary;
import com.moviebooking.dto.TicketView;
import com.moviebooking.entity.Customer;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.Screen;
import com.moviebooking.entity.Show;
import com.moviebooking.entity.Theatre;
import com.moviebooking.entity.Ticket;
import com.moviebooking.entity.TicketBooking;
import com.moviebooking.repository.IBookingRepository;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.repository.IMovieRepository;
import com.moviebooking.repository.IScreenRepository;
import com.moviebooking.repository.IShowRepository;
import com.moviebooking.repository.ITheatreRepository;
import com.moviebooking.service.IBookingService;

@Service
public class BookingServiceImpl implements IBookingService {

    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IShowRepository showRepository;

    @Autowired
    private IMovieRepository movieRepository;

    @Autowired
    private ITheatreRepository theatreRepository;

    @Autowired
    private IScreenRepository screenRepository;

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

        if (request.getPaymentIntentId() != null && !request.getPaymentIntentId().isBlank()) {
            booking.setPaymentReference(request.getPaymentIntentId());
        }

        String paymentMode = request.getPaymentMode();
        if (paymentMode == null || paymentMode.isBlank()) {
            paymentMode = (request.getPaymentIntentId() != null && !request.getPaymentIntentId().isBlank()) ? "CARD" : "ONLINE";
        }
        booking.setTransactionMode(paymentMode.toUpperCase());
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
    @Transactional(readOnly = true)
    public List<TicketView> findBookingsForCustomer(int customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }

        List<TicketBooking> bookings = bookingRepository.findByCustomerCustomerId(customerId);
        if (bookings.isEmpty()) {
            return List.of();
        }

        Comparator<TicketView> byShowStart = Comparator.comparing(
                TicketView::getShowStartTime,
                Comparator.nullsLast(LocalDateTime::compareTo)
        );

        Comparator<TicketView> byBookingDate = Comparator.comparing(
                TicketView::getBookingDate,
                Comparator.nullsLast(LocalDate::compareTo)
        );

        return bookings.stream()
                .map(this::mapToTicketView)
                .sorted(byShowStart.thenComparing(byBookingDate).thenComparing(TicketView::getBookingId))
                .collect(Collectors.toList());
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

    private TicketView mapToTicketView(TicketBooking booking) {
        Ticket ticket = booking.getTicket();
        List<String> seats = ticket != null && ticket.getSeatNumber() != null
                ? List.copyOf(ticket.getSeatNumber())
                : List.of();

        Show show = showRepository.findById(booking.getShowId()).orElse(null);
        Movie movie = resolveMovie(show);
        Screen screen = resolveScreen(show);
        Theatre theatre = resolveTheatre(show, screen);

        return new TicketView(
                booking.getBookingId(),
                ticket != null ? ticket.getTicketId() : null,
                booking.getBookingDate(),
                ticket != null ? ticket.getBookingRef() : null,
                booking.getTransactionId(),
                booking.getTransactionMode(),
                booking.getTransactionStatus(),
                booking.getPaymentReference(),
                booking.getTotalCost(),
                seats,
                ticket != null ? ticket.getNoOfSeats() : seats.size(),
                show != null ? show.getShowId() : null,
                show != null ? show.getShowName() : null,
                show != null ? show.getShowStartTime() : null,
                show != null ? show.getShowEndTime() : null,
                movie != null ? movie.getMovieId() : (show != null ? show.getMovieId() : null),
                movie != null ? movie.getMovieName() : null,
                movie != null ? movie.getMovieGenre() : null,
                movie != null ? movie.getLanguage() : null,
                movie != null ? movie.getImageUrl() : null,
                theatre != null ? theatre.getTheatreId() : (show != null ? show.getTheatreId() : null),
                theatre != null ? theatre.getTheatreName() : null,
                theatre != null ? theatre.getTheatreCity() : null,
                screen != null ? screen.getScreenId() : (show != null ? show.getScreenId() : null),
                screen != null ? screen.getScreenName() : null
        );
    }

    private Movie resolveMovie(Show show) {
        if (show == null) {
            return null;
        }
        if (show.getMovie() != null) {
            return show.getMovie();
        }
        Integer movieId = show.getMovieId();
        if (movieId == null) {
            return null;
        }
        return movieRepository.findById(movieId).orElse(null);
    }

    private Screen resolveScreen(Show show) {
        if (show == null) {
            return null;
        }
        if (show.getScreen() != null) {
            return show.getScreen();
        }
        int screenId = show.getScreenId();
        if (screenId <= 0) {
            return null;
        }
        return screenRepository.findById(screenId).orElse(null);
    }

    private Theatre resolveTheatre(Show show, Screen screen) {
        if (show != null && show.getTheatreId() > 0) {
            Optional<Theatre> theatre = theatreRepository.findById(show.getTheatreId());
            if (theatre.isPresent()) {
                return theatre.get();
            }
        }
        if (screen != null && screen.getTheatreId() > 0) {
            return theatreRepository.findById(screen.getTheatreId()).orElse(null);
        }
        return null;
    }
}
