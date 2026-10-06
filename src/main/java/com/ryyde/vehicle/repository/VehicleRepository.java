package com.ryyde.vehicle.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ryyde.vehicle.model.Vehicle;

import java.util.List;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {

    List<Vehicle> findByOwnerId(String ownerId);
}
