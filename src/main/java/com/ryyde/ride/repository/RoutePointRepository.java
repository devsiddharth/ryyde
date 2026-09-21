package com.ryyde.ride.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ryyde.ride.model.RoutePoint;

public interface RoutePointRepository extends MongoRepository<RoutePoint, String> {
}
