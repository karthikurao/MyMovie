package com.moviebooking.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.config.JwtTokenProvider;
import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.MovieBookingSummary;
import com.moviebooking.dto.TicketView;
import com.moviebooking.entity.Ticket;
import com.moviebooking.entity.TicketBooking;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.repository.IUserRepository;
import com.moviebooking.service.IBookingService;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("BookingController")
@SuppressWarnings("unused")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookingService bookingService;

    @MockBean
    private IUserRepository userRepository;

    @MockBean
    private ICustomerRepository customerRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/bookings returns collection")
    void showAllBookings_returnsOk() throws Exception {
        TicketBooking booking = new TicketBooking();
        booking.setBookingId(1);
        booking.setShowId(10);
        booking.setBookingDate(LocalDate.of(2025, 10, 1));
        booking.setTransactionId(12345);
        booking.setPaymentReference("pi_test_123");
        booking.setTransactionMode("ONLINE");
        booking.setTransactionStatus("CONFIRMED");
        booking.setTotalCost(450.0);

        when(bookingService.showAllBookings()).thenReturn(List.of(booking));

        mockMvc.perform(get("/api/bookings")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(booking.getBookingId()))
                .andExpect(jsonPath("$[0].transactionStatus").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /api/bookings propagates server error")
    void showAllBookings_handlesFailure() throws Exception {
        when(bookingService.showAllBookings()).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/bookings")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/bookings creates booking")
    void addBooking_success() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setShowId(12);
        request.setCustomerId(5);
        request.setSeatNumbers(List.of("A1", "A2"));
        request.setTotalCost(800.0);
        request.setBookingDate(LocalDate.of(2025, 10, 1));
        request.setPaymentMode("UPI");
        request.setPaymentIntentId("pi_test_999");

        Ticket ticket = new Ticket();
        ticket.setTicketId(3);
        ticket.setNoOfSeats(2);
        ticket.setSeatNumber(List.of("A1", "A2"));
        ticket.setBookingRef(1234567);
        ticket.setTicketStatus(true);

        TicketBooking booking = new TicketBooking();
        booking.setBookingId(10);
        booking.setShowId(request.getShowId());
        booking.setBookingDate(request.getBookingDate());
        booking.setTransactionId(999999);
        booking.setPaymentReference("pi_test_999");
        booking.setTransactionMode("UPI");
        booking.setTransactionStatus("CONFIRMED");
        booking.setTotalCost(request.getTotalCost());
        booking.setTicket(ticket);
        ticket.setBooking(booking);

        when(bookingService.addBooking(any(BookingRequest.class))).thenReturn(booking);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(booking.getBookingId()))
                .andExpect(jsonPath("$.transactionMode").value("UPI"));
    }

    @Test
    @DisplayName("POST /api/bookings returns 400 on validation error")
    void addBooking_validationError() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setShowId(12);
        request.setCustomerId(5);
        request.setSeatNumbers(List.of());
        request.setTotalCost(0.0);

        when(bookingService.addBooking(any(BookingRequest.class)))
                .thenThrow(new IllegalArgumentException("At least one seat must be selected"));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("At least one seat must be selected"));
    }

    @Test
    @DisplayName("GET /api/bookings/summary/movies returns aggregated metrics")
    void summarizeBookingsByMovie_returnsOk() throws Exception {
        MovieBookingSummary summary = new MovieBookingSummary(2, "Inception", 3L, 9L, 2450.0);
        when(bookingService.summarizeBookingsByMovie()).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/bookings/summary/movies")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].movieId").value(summary.getMovieId()))
                .andExpect(jsonPath("$[0].totalBookings").value((int) summary.getTotalBookings()))
                .andExpect(jsonPath("$[0].totalRevenue").value(summary.getTotalRevenue()));
    }

    @Test
    @DisplayName("GET /api/bookings/customer/{id} returns ticket views")
    void getBookingsForCustomer_returnsTickets() throws Exception {
        TicketView ticket = new TicketView(
                101,
                55,
                LocalDate.of(2025, 11, 4),
                7654321,
                998877,
                "CARD",
                "CONFIRMED",
                "pi_test_123",
                1250.0,
                List.of("A1", "A2"),
                2,
                22,
                "Evening Show",
                LocalDateTime.of(2025, 11, 30, 19, 0),
                LocalDateTime.of(2025, 11, 30, 21, 30),
                9,
                "Interstellar",
                "Sci-Fi",
                "English",
                "https://example.com/poster.jpg",
                4,
                "Galaxy Cinemas",
                "Bengaluru",
                7,
                "Screen 2"
        );

        when(bookingService.findBookingsForCustomer(5)).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/bookings/customer/{id}", 5)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(ticket.getBookingId()))
                .andExpect(jsonPath("$[0].seatNumbers[0]").value("A1"))
                .andExpect(jsonPath("$[0].movieName").value("Interstellar"));
    }

    @Test
    @DisplayName("GET /api/bookings/customer/{id} returns 404 when customer missing")
    void getBookingsForCustomer_handlesMissingCustomer() throws Exception {
        when(bookingService.findBookingsForCustomer(999)).thenThrow(new IllegalArgumentException("Customer not found with ID: 999"));

        mockMvc.perform(get("/api/bookings/customer/{id}", 999)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer not found with ID: 999"));
    }
}
