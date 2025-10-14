package com.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.MovieBookingSummary;
import com.moviebooking.entity.Customer;
import com.moviebooking.entity.Show;
import com.moviebooking.repository.IBookingRepository;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.repository.IShowRepository;
import com.moviebooking.service.impl.BookingServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private IBookingRepository bookingRepository;

    @Mock
    private ICustomerRepository customerRepository;

    @Mock
    private IShowRepository showRepository;

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
}
