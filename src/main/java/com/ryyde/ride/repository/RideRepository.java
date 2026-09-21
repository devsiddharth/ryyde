package com.ryyde.ride.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ryyde.ride.model.Ride;

public interface RideRepository extends MongoRepository<Ride, String> {
}
