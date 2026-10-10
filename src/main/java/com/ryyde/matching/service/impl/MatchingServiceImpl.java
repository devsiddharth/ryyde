package com.ryyde.matching.service.impl;

import com.ryyde.matching.algorithm.RouteMatchPoint;
import com.ryyde.matching.algorithm.RouteMatchingAlgorithm;
import com.ryyde.matching.service.MatchingService;
import com.ryyde.ride.model.Location;
import com.ryyde.ride.model.Ride;
import com.ryyde.ride.model.RoutePoint;
import com.ryyde.ride.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.ryyde.common.enums.RideStatus.ACTIVE;

@Service
@RequiredArgsConstructor

public class MatchingServiceImpl implements MatchingService {

    private final RideRepository rideRepository;
    private final RouteMatchingAlgorithm algorithm;



    @Override
    public List<Ride> findMatchingRides(Location pickupLocation, Location dropLocation) {

        List<Ride> rides = rideRepository.findAll();

        List<Ride> mathcingRides = new ArrayList<>();

        for (Ride ride: rides){

            // 1. Is ride active?
            // 2. Does it have available seats?
            // 3. Find pickup position on route
            // 4. Find drop position on route
            // 5. Are pickup/drop close enough to route?
            // 6. Is pickup before drop?
            // 7. If yes → add ride

            if (ride.getStatus() != ACTIVE){
                continue;
            }

            if (ride.getAvailableSeats() < 1){
                continue;
            }

            RouteMatchPoint pickupMatch = algorithm.findNearestRoutePoint(pickupLocation,ride.getRoute());

            RouteMatchPoint dropMatch = algorithm.findNearestRoutePoint(dropLocation, ride.getRoute());

            RoutePoint pickupPoint = pickupMatch.getRoutePoint();
            RoutePoint dropPoint = dropMatch.getRoutePoint();

            double pickupDeviation = pickupMatch.getDeviationKm();
            double dropDeviation = dropMatch.getDeviationKm();

            if (pickupMatch.getRoutePoint() == null || dropMatch.getRoutePoint() == null){
                continue;
            }

            if (pickupPoint.getDistanceFromStartKm() >= dropPoint.getDistanceFromStartKm()){
                continue;
            }

            if (pickupDeviation> 2.0 || dropDeviation>2.0){
                continue;
            }

            mathcingRides.add(ride);

        }

        return mathcingRides;
    }
}
