package com.moviebooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "ticket_bookings")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TicketBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int bookingId;

    @NotNull(message = "Show ID is required")
    @Column(nullable = false)
    private int showId;

    @NotNull(message = "Booking date is required")
    @Column(nullable = false)
    private LocalDate bookingDate;

    @NotNull(message = "Transaction ID is required")
    @Column(nullable = false)
    private int transactionId;

    @Column(name = "payment_reference", length = 64)
    private String paymentReference;

    @NotBlank(message = "Transaction mode is required")
    @Column(nullable = false)
    private String transactionMode;

    @NotBlank(message = "Transaction status is required")
    @Column(nullable = false)
    private String transactionStatus;

    @NotNull(message = "Total cost is required")
    @Column(nullable = false)
    private double totalCost;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ticketId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    @JsonIgnoreProperties({"bookings", "hibernateLazyInitializer", "handler"})
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"bookings", "movie", "screen", "hibernateLazyInitializer", "handler"})
    private Show show;

    // Constructors
    public TicketBooking() {
    }

    public TicketBooking(int showId, LocalDate bookingDate, int transactionId,
            String transactionMode, String transactionStatus, double totalCost) {
        this.showId = showId;
        this.bookingDate = bookingDate;
        this.transactionId = transactionId;
        this.transactionMode = transactionMode;
        this.transactionStatus = transactionStatus;
        this.totalCost = totalCost;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }
}
