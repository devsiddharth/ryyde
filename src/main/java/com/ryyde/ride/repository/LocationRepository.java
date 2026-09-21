package com.ryyde.ride.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ryyde.ride.model.Location;

public interface LocationRepository extends MongoRepository<Location, String> {
}
