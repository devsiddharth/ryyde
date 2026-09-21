package com.ryyde.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ryyde.booking.model.Booking;

public interface BookingRepository extends MongoRepository<Booking, String> {
}
