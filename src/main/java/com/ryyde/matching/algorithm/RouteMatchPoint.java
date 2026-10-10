package com.ryyde.matching.algorithm;

import com.ryyde.ride.model.RoutePoint;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteMatchPoint {

    private RoutePoint routePoint;
    private double deviationKm;
}
