package com.ryyde.booking.service;

import java.util.List;

import com.ryyde.booking.model.Booking;
import com.ryyde.common.enums.BookingStatus;

public interface BookingService {

    Booking createBooking(Booking booking);

    Booking getBookingById(String id);

    List<Booking> getAllBookings();

    List<Booking> getBookingsByRide(String rideId);

    List<Booking> getBookingsByPassenger(String passengerId);

    Booking updateBookingStatus(String id, BookingStatus status);

    Booking cancelBooking(String id);

    Booking completeBooking(String id);
}
