package com.ryyde.matching.dto;

import com.ryyde.ride.model.Location;
import lombok.Data;

@Data
public class MatchRidesRequest {

    public Location pickupLocation;
    public Location dropLocation;

}
