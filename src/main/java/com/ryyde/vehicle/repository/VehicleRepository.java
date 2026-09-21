package com.ryyde.vehicle.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ryyde.vehicle.model.Vehicle;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {
}
