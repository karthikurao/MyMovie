package com.moviebooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "tickets")
@JsonIgnoreProperties({"booking", "hibernateLazyInitializer", "handler"})
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ticketId;

    @NotNull(message = "Number of seats is required")
    @Column(nullable = false)
    private int noOfSeats;

    @ElementCollection
    @CollectionTable(name = "ticket_seats", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "seat_number")
    private List<String> seatNumber;

    @NotNull(message = "Booking reference is required")
    @Column(nullable = false)
    private int bookingRef;

    @NotNull(message = "Ticket status is required")
    @Column(nullable = false)
    private boolean ticketStatus;

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TicketBooking booking;

    // Constructors
    public Ticket() {}

    public Ticket(int noOfSeats, List<String> seatNumber, int bookingRef, boolean ticketStatus) {
        this.noOfSeats = noOfSeats;
        this.seatNumber = seatNumber;
        this.bookingRef = bookingRef;
        this.ticketStatus = ticketStatus;
    }

    // Getters and Setters
    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(int noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    public List<String> getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(List<String> seatNumber) {
        this.seatNumber = seatNumber;
    }

    public int getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(int bookingRef) {
        this.bookingRef = bookingRef;
    }

    public boolean isTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(boolean ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public TicketBooking getBooking() {
        return booking;
    }

    public void setBooking(TicketBooking booking) {
        this.booking = booking;
    }
}
