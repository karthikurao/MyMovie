package com.moviebooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seatId;

    @NotBlank(message = "Seat number is required")
    @Column(nullable = false)
    private String seatNumber;

    @NotBlank(message = "Seat type is required")
    @Column(nullable = false)
    private String type;

    @NotNull(message = "Price is required")
    @Column(nullable = false)
    private double price;

    // Constructors
    public Seat() {}

    public Seat(String seatNumber, String type, double price) {
        this.seatNumber = seatNumber;
        this.type = type;
        this.price = price;
    }

    // Getters and Setters
    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
