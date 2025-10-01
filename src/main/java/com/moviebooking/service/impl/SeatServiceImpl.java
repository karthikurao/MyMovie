package com.moviebooking.service.impl;

import com.moviebooking.entity.Seat;
import com.moviebooking.repository.ISeatRepository;
import com.moviebooking.service.ISeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SeatServiceImpl implements ISeatService {

    @Autowired
    private ISeatRepository seatRepository;

    @Override
    public Seat bookSeat(Seat seat) {
        Optional<Seat> existingSeat = seatRepository.findById(seat.getSeatId());
        if (existingSeat.isPresent()) {
            // Mark seat as booked (you might want to add a status field to Seat entity)
            return seatRepository.save(seat);
        } else {
            throw new RuntimeException("Seat not found with ID: " + seat.getSeatId());
        }
    }

    @Override
    public Seat cancelSeatBooking(Seat seat) {
        Optional<Seat> existingSeat = seatRepository.findById(seat.getSeatId());
        if (existingSeat.isPresent()) {
            // Mark seat as available
            return seatRepository.save(seat);
        } else {
            throw new RuntimeException("Seat not found with ID: " + seat.getSeatId());
        }
    }

    @Override
    public Seat blockSeat(Seat seat) {
        Optional<Seat> existingSeat = seatRepository.findById(seat.getSeatId());
        if (existingSeat.isPresent()) {
            // Mark seat as blocked
            return seatRepository.save(seat);
        } else {
            throw new RuntimeException("Seat not found with ID: " + seat.getSeatId());
        }
    }
}
