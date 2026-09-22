package com.ryyde.ride.service.implementations;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ryyde.common.enums.RideStatus;
import com.ryyde.common.exception.ResourceNotFoundException;
import com.ryyde.ride.model.Ride;
import com.ryyde.ride.repository.RideRepository;
import com.ryyde.ride.service.RideService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;

    @Override
    public Ride createRide(Ride ride) {
        if (ride.getStatus() == null) {
            ride.setStatus(RideStatus.SCHEDULED);
        }
        if (ride.getAvailableSeats() == null) {
            ride.setAvailableSeats(ride.getTotalSeats());
        }
        if (ride.getCreatedAt() == null) {
            ride.setCreatedAt(Instant.now());
        }
        return rideRepository.save(ride);
    }

    @Override
    public Ride getRideById(String id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + id));
    }

    @Override
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    @Override
    public Ride updateRide(String id, Ride ride) {
        Ride existingRide = getRideById(id);

        existingRide.setVehicleId(ride.getVehicleId());
        existingRide.setStartLocation(ride.getStartLocation());
        existingRide.setEndLocation(ride.getEndLocation());
        existingRide.setRoute(ride.getRoute());
        existingRide.setDepartureTime(ride.getDepartureTime());
        existingRide.setEstimatedArrivalTime(ride.getEstimatedArrivalTime());
        existingRide.setDistanceKm(ride.getDistanceKm());
        existingRide.setTotalSeats(ride.getTotalSeats());
        existingRide.setSharingCharge(ride.getSharingCharge());
        existingRide.setStatus(ride.getStatus());

        return rideRepository.save(existingRide);
    }

    @Override
    public Ride cancelRide(String id) {
        Ride existingRide = getRideById(id);
        existingRide.setStatus(RideStatus.CANCELLED);
        return rideRepository.save(existingRide);
    }

    @Override
    public List<Ride> getRidesByDriver(String driverId) {
        return rideRepository.findAll().stream()
                .filter(ride -> driverId.equals(ride.getDriverId()))
                .toList();
    }

    @Override
    public List<Ride> getRidesByStatus(RideStatus status) {
        return rideRepository.findAll().stream()
                .filter(ride -> status == ride.getStatus())
                .toList();
    }
}
