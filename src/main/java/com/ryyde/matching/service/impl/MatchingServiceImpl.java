package com.ryyde.matching.service.impl;

import com.ryyde.matching.service.MatchingService;
import com.ryyde.ride.model.Location;
import com.ryyde.ride.model.Ride;
import com.ryyde.ride.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class MatchingServiceImpl implements MatchingService {

    private final RideRepository rideRepository;

    @Override
    public List<Ride> findMatchingRides(Location pickupLocation, Location dropLocation) {
        return List.of();
    }
}
