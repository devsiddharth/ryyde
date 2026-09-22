package com.ryyde.ride.service;

import java.util.List;

import com.ryyde.common.enums.RideStatus;
import com.ryyde.ride.model.Ride;

public interface RideService {

    Ride createRide(Ride ride);

    Ride getRideById(String id);

    List<Ride> getAllRides();

    Ride updateRide(String id, Ride ride);

    Ride cancelRide(String id);

    List<Ride> getRidesByDriver(String driverId);

    List<Ride> getRidesByStatus(RideStatus status);
}
