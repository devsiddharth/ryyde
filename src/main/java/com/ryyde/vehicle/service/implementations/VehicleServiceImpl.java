package com.ryyde.vehicle.service.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ryyde.common.exception.ResourceNotFoundException;
import com.ryyde.vehicle.model.Vehicle;
import com.ryyde.vehicle.repository.VehicleRepository;
import com.ryyde.vehicle.service.VehicleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle getVehicleById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public Vehicle updateVehicle(String id, Vehicle vehicle) {
        Vehicle existingVehicle = getVehicleById(id);

        existingVehicle.setType(vehicle.getType());
        existingVehicle.setBrand(vehicle.getBrand());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setRegistrationNumber(vehicle.getRegistrationNumber());
        existingVehicle.setSeatCapacity(vehicle.getSeatCapacity());

        return vehicleRepository.save(existingVehicle);
    }

    @Override
    public void deleteVehicle(String id) {
        Vehicle existingVehicle = getVehicleById(id);
        vehicleRepository.delete(existingVehicle);
    }

    @Override
    public List<Vehicle> getVehiclesByOwner(String ownerId) {
        return vehicleRepository.findAll().stream()
                .filter(vehicle -> ownerId.equals(vehicle.getOwnerId()))
                .toList();
    }
}
