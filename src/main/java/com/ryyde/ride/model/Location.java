package com.ryyde.ride.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "locations")
public class Location {

    @MongoId
    private String id;

    private String type = "Point";

    private double[] coordinates;
}
