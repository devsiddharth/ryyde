package com.ryyde.rideRequest.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ryyde.rideRequest.model.RideRequest;

public interface RideRequestRepository extends MongoRepository<RideRequest, String> {
}
