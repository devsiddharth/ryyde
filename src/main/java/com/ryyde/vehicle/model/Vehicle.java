package com.ryyde.vehicle.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.ryyde.common.enums.VehicleType;
import lombok.Data;

@Data
@Document(collection = "vehicles")
public class Vehicle {

    @MongoId
    private String id;

    private String ownerId;

    private VehicleType type;

    private String brand;

    private String model;

    private String registrationNumber;

    private Integer seatCapacity;
}
