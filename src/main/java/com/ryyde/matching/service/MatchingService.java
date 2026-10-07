package com.ryyde.matching.service;

import com.ryyde.ride.model.Location;
import com.ryyde.ride.model.Ride;

import java.util.List;

public interface MatchingService {

    List<Ride> findMatchingRides(Location pickupLocation, Location dropLocation);
}
