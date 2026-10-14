package com.ryyde.matching.controller;


import com.ryyde.matching.dto.MatchRidesRequest;
import com.ryyde.matching.dto.MatchedRideResponse;
import com.ryyde.matching.service.MatchingService;
import com.ryyde.ride.model.Ride;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor

public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping("/rides")
public ResponseEntity<List<MatchedRideResponse>> findMatchingRides(@RequestBody MatchRidesRequest request){

        List<Ride> rides = matchingService.findMatchingRides(request.getPickupLocation(), request.getDropLocation());

        List<MatchedRideResponse> responses = rides.stream()
                .map(ride -> MatchedRideResponse.builder()
                        .rideId(ride.getId())
                        .driverId(ride.getDriverId())
                        .vehicleId(ride.getVehicleId())
                        .startLocation(ride.getStartLocation())
                        .endLocation(ride.getEndLocation())
                        .departureTime(ride.getDepartureTime())
                        .estimatedArrivalTime(ride.getEstimatedArrivalTime())
                        .distanceKm(ride.getDistanceKm())
                        .availableSeats(ride.getAvailableSeats())
                        .sharingCharge(ride.getSharingCharge())
                        .status(ride.getStatus()).build())
                .toList();

        return ResponseEntity.ok(responses);
    }

}
