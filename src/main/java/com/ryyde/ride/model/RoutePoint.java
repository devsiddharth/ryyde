package com.ryyde.ride.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "route_points")
public class RoutePoint {

    @MongoId
    private String id;

    private double latitude;

    private double longitude;

    private double distanceFromStartKm;
}
