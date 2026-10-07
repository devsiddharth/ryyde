package com.ryyde.matching.algorithm;

import com.ryyde.ride.model.Location;
import com.ryyde.ride.model.RoutePoint;

import java.util.List;

public class RouteMatchingAlgorithm {

    public RoutePoint findNearestRoutePoint(
            Location location,
            List<RoutePoint> route) {

        RoutePoint nearestPoint = null;
        double shortestDistance = Double.MAX_VALUE;

        double longitude = location.getCoordinates()[0];
        double latitude = location.getCoordinates()[1];

        for (RoutePoint routePoint : route) {

            double distance = calculateDistanceKm(
                    latitude,
                    longitude,
                    routePoint.getLatitude(),
                    routePoint.getLongitude()
            );

            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestPoint = routePoint;
            }
        }

        return nearestPoint;
    }

    private double calculateDistanceKm(
            double lat1,
            double lat2,
            double long1,
            double long2){

        // Distance method - Haversine calculation will go here

        final double EARTH_RADIUS_KM = 6371.0;

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);


        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLong = Math.toRadians(long2 - long1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return EARTH_RADIUS_KM * c;

    }
}
