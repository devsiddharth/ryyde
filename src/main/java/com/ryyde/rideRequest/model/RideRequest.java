package com.ryyde.rideRequest.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.ryyde.common.enums.RideRequestStatus;
import com.ryyde.ride.model.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "ride_requests")
public class RideRequest {

    @MongoId
    private String id;

    private String rideId;

    private String passengerId;

    private Location pickupLocation;

    private Location dropLocation;

    private BigDecimal estimatedCharge;

    private RideRequestStatus status;

    private Instant createdAt;
}
