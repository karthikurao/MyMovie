package com.moviebooking.repository;

import com.moviebooking.dto.MovieBookingSummary;
import com.moviebooking.entity.TicketBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IBookingRepository extends JpaRepository<TicketBooking, Integer> {
    List<TicketBooking> findByShowId(int showId);
    List<TicketBooking> findByShowMovieId(int movieId);
    List<TicketBooking> findByBookingDate(LocalDate date);
    List<TicketBooking> findByCustomerCustomerId(int customerId);

    @Query("SELECT seat FROM TicketBooking tb JOIN tb.ticket.seatNumber seat WHERE tb.showId = :showId AND UPPER(tb.transactionStatus) <> 'CANCELLED'")
    List<String> findReservedSeatNumbersByShow(@Param("showId") int showId);

    @Query("""
        SELECT new com.moviebooking.dto.MovieBookingSummary(
            m.movieId,
            m.movieName,
            COUNT(tb),
            COALESCE(SUM(t.noOfSeats), 0),
            COALESCE(SUM(tb.totalCost), 0.0)
        )
        FROM TicketBooking tb
        JOIN tb.show s
        JOIN s.movie m
        JOIN tb.ticket t
        WHERE UPPER(tb.transactionStatus) <> 'CANCELLED'
        GROUP BY m.movieId, m.movieName
        ORDER BY m.movieName ASC
    """)
    List<MovieBookingSummary> summarizeBookingsByMovie();

    @Query("SELECT SUM(tb.totalCost) FROM TicketBooking tb WHERE tb.bookingId = :bookingId")
    double calculateTotalCost(@Param("bookingId") int bookingId);
}
