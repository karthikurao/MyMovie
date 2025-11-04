package com.moviebooking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.MovieBookingSummary;
import com.moviebooking.dto.TicketView;
import com.moviebooking.entity.Customer;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.Screen;
import com.moviebooking.entity.Show;
import com.moviebooking.entity.Theatre;
import com.moviebooking.entity.TicketBooking;
import com.moviebooking.repository.IBookingRepository;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.repository.IMovieRepository;
import com.moviebooking.repository.IScreenRepository;
import com.moviebooking.repository.IShowRepository;
import com.moviebooking.repository.ITheatreRepository;
import com.moviebooking.service.impl.BookingServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private IBookingRepository bookingRepository;

    @Mock
    private ICustomerRepository customerRepository;

    @Mock
    private IShowRepository showRepository;

    @Mock
    private IMovieRepository movieRepository;

    @Mock
    private ITheatreRepository theatreRepository;

    @Mock
    private IScreenRepository screenRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void testCalculateTotalCost_Success() {
        // Given
        int bookingId = 1;
        double expectedCost = 450.0;
        when(bookingRepository.calculateTotalCost(bookingId)).thenReturn(expectedCost);

        // When
        double result = bookingService.calculateTotalCost(bookingId);

        // Then
        assertEquals(expectedCost, result);
        verify(bookingRepository).calculateTotalCost(bookingId);
    }

    @Test
    void addBooking_ThrowsWhenSeatAlreadyReserved() {
        BookingRequest request = new BookingRequest();
        request.setCustomerId(1);
        request.setShowId(2);
        request.setSeatNumbers(List.of("A1", "A2"));
        request.setTotalCost(500.0);

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCustomerName("Test User");
        customer.setAddress("123 Street");
        customer.setMobileNumber("9999999999");
        customer.setEmail("test@example.com");
        customer.setPassword("secret");

        Show show = new Show();
        show.setShowId(2);
        show.setMovieId(5);
        show.setShowName("Morning Show");
        show.setShowStartTime(LocalDateTime.now());
        show.setShowEndTime(LocalDateTime.now().plusHours(3));
        show.setScreenId(1);
        show.setTheatreId(1);

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(showRepository.findById(2)).thenReturn(Optional.of(show));
        when(bookingRepository.findReservedSeatNumbersByShow(2)).thenReturn(List.of("A2"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(request));
        assertEquals("Selected seats are no longer available: [A2]", exception.getMessage());
    }

    @Test
    void addBooking_ThrowsWhenDuplicateSeatsProvided() {
        BookingRequest request = new BookingRequest();
        request.setCustomerId(1);
        request.setShowId(2);
        request.setSeatNumbers(List.of("A1", "a1"));
        request.setTotalCost(500.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(request));
        assertEquals("Duplicate seats selected. Please review your selection.", exception.getMessage());
    }

    @Test
    void summarizeBookingsByMovie_DelegatesToRepository() {
        MovieBookingSummary summary = new MovieBookingSummary(2, "Inception", 4L, 12L, 3200.0);
        when(bookingRepository.summarizeBookingsByMovie()).thenReturn(List.of(summary));

        var result = bookingService.summarizeBookingsByMovie();

        assertEquals(1, result.size());
        assertEquals(summary.getMovieName(), result.get(0).getMovieName());
        verify(bookingRepository).summarizeBookingsByMovie();
    }

    @Test
    void findBookingsForCustomer_ThrowsWhenCustomerMissing() {
        int customerId = 42;
        when(customerRepository.existsById(customerId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()
                -> bookingService.findBookingsForCustomer(customerId));

        assertEquals("Customer not found with ID: 42", exception.getMessage());
    }

    @Test
    void findBookingsForCustomer_ReturnsTicketViews() {
        int customerId = 7;
        int showId = 11;
        int movieId = 5;
        int theatreId = 3;
        int screenId = 9;

        TicketBooking booking = new TicketBooking();
        booking.setBookingId(123);
        booking.setCustomer(new Customer());
        booking.setShowId(showId);
        booking.setBookingDate(LocalDate.of(2024, 5, 1));
        booking.setTransactionId(987654);
        booking.setTransactionMode("CARD");
        booking.setTransactionStatus("CONFIRMED");
        booking.setTotalCost(1250.0);

        var ticket = new com.moviebooking.entity.Ticket();
        ticket.setTicketId(55);
        ticket.setSeatNumber(List.of("A1", "A2"));
        ticket.setNoOfSeats(2);
        ticket.setBookingRef(8765432);
        ticket.setTicketStatus(true);
        booking.setTicket(ticket);

        Show show = new Show();
        show.setShowId(showId);
        show.setMovieId(movieId);
        show.setTheatreId(theatreId);
        show.setScreenId(screenId);
        show.setShowName("Evening Premiere");
        show.setShowStartTime(LocalDateTime.of(2024, 5, 20, 19, 0));
        show.setShowEndTime(LocalDateTime.of(2024, 5, 20, 21, 30));

        Movie movie = new Movie();
        movie.setMovieId(movieId);
        movie.setMovieName("The Great Adventure");
        movie.setMovieGenre("Action");
        movie.setLanguage("English");

        Theatre theatre = new Theatre();
        theatre.setTheatreId(theatreId);
        theatre.setTheatreName("Galaxy Cinema");
        theatre.setTheatreCity("Bengaluru");

        Screen screen = new Screen();
        screen.setScreenId(screenId);
        screen.setScreenName("Screen 3");
        screen.setTheatreId(theatreId);

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(bookingRepository.findByCustomerCustomerId(customerId)).thenReturn(List.of(booking));
        when(showRepository.findById(showId)).thenReturn(Optional.of(show));
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(theatreRepository.findById(theatreId)).thenReturn(Optional.of(theatre));
        when(screenRepository.findById(screenId)).thenReturn(Optional.of(screen));

        List<TicketView> result = bookingService.findBookingsForCustomer(customerId);

        assertEquals(1, result.size());
        TicketView view = result.get(0);
        assertEquals(booking.getBookingId(), view.getBookingId());
        assertEquals(ticket.getSeatNumber(), view.getSeatNumbers());
        assertEquals(show.getShowStartTime(), view.getShowStartTime());
        assertEquals(movie.getMovieName(), view.getMovieName());
        assertEquals(theatre.getTheatreName(), view.getTheatreName());
        assertTrue(view.getSeatNumbers().contains("A1"));
    }
}
