package com.ryyde.booking.service.implementations;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ryyde.booking.model.Booking;
import com.ryyde.booking.repository.BookingRepository;
import com.ryyde.booking.service.BookingService;
import com.ryyde.common.enums.BookingStatus;
import com.ryyde.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(Booking booking) {
        if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.REQUESTED);
        }
        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(Instant.now());
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getBookingsByRide(String rideId) {
        return bookingRepository.findAll().stream()
                .filter(booking -> rideId.equals(booking.getRideId()))
                .toList();
    }

    @Override
    public List<Booking> getBookingsByPassenger(String passengerId) {
        return bookingRepository.findAll().stream()
                .filter(booking -> passengerId.equals(booking.getPassengerId()))
                .toList();
    }

    @Override
    public Booking updateBookingStatus(String id, BookingStatus status) {
        Booking existingBooking = getBookingById(id);
        existingBooking.setStatus(status);
        return bookingRepository.save(existingBooking);
    }

    @Override
    public Booking cancelBooking(String id) {
        return updateBookingStatus(id, BookingStatus.CANCELLED);
    }

    @Override
    public Booking completeBooking(String id) {
        return updateBookingStatus(id, BookingStatus.COMPLETED);
    }
}
