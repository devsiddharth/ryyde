package com.ryyde.booking.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.ryyde.common.enums.BookingStatus;
import com.ryyde.ride.model.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "bookings")
public class Booking {

    @MongoId
    private String id;

    private String rideId;

    private String passengerId;

    private Location pickupLocation;

    private Location dropLocation;

    private BigDecimal sharingCharge;

    private BookingStatus status;

    private Instant createdAt;
}
