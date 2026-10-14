package com.ryyde.matching.dto;

import com.ryyde.common.enums.RideStatus;
import com.ryyde.ride.model.Location;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MatchedRideResponse {
    private String rideId;
    private String driverId;
    private String vehicleId;

    private Location startLocation;
    private Location endLocation;

    private LocalDateTime departureTime;
    private LocalDateTime estimatedArrivalTime;

    private Double distanceKm;
    private Integer availableSeats;
    private BigDecimal sharingCharge;
    private RideStatus status;
}
