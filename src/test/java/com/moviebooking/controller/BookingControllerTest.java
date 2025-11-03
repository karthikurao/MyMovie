package com.moviebooking.controller;

import java.time.LocalDate;
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
import com.moviebooking.entity.Ticket;
import com.moviebooking.entity.TicketBooking;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.repository.IUserRepository;
import com.moviebooking.service.IBookingService;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("BookingController")
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
}
