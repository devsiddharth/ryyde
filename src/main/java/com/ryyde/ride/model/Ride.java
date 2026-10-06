package com.ryyde.ride.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.ryyde.common.enums.RideStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "rides")
public class Ride {

    @MongoId
    private String id;

    private String driverId;

    private String vehicleId;

    private Location startLocation;

    private Location endLocation;

    private List<RoutePoint> route;

    private LocalDateTime departureTime;

    private LocalDateTime estimatedArrivalTime;

    private Double distanceKm;

    private Integer totalSeats;

    private Integer availableSeats;

    private BigDecimal sharingCharge;

    private RideStatus status;

    private Instant createdAt;
}


