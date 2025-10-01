package com.moviebooking.service;

import com.moviebooking.entity.Seat;

public interface ISeatService {
    Seat bookSeat(Seat seat);
    Seat cancelSeatBooking(Seat seat);
    Seat blockSeat(Seat seat);
}
