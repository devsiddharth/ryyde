package com.ryyde.vehicle.service;

import java.util.List;

import com.ryyde.vehicle.model.Vehicle;

public interface VehicleService {

    Vehicle createVehicle(Vehicle vehicle);

    Vehicle getVehicleById(String id);

    List<Vehicle> getAllVehicles();

    Vehicle updateVehicle(String id, Vehicle vehicle);

    void deleteVehicle(String id);

    List<Vehicle> getVehiclesByOwner(String ownerId);
}
