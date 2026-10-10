package com.ryyde.matching.algorithm;

import com.ryyde.ride.model.Location;
import com.ryyde.ride.model.RoutePoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteMatchingAlgorithmTest {

    @Test
    void shouldFindNearestRoutePoint() {

        RoutePoint pointA = new RoutePoint();
        pointA.setLatitude(17.3850);
        pointA.setLongitude(78.4867);
        pointA.setDistanceFromStartKm(0.0);

        RoutePoint pointB = new RoutePoint();
        pointB.setLatitude(17.4000);
        pointB.setLongitude(78.5000);
        pointB.setDistanceFromStartKm(2.0);

        RoutePoint pointC = new RoutePoint();
        pointC.setLatitude(17.4150);
        pointC.setLongitude(78.5150);
        pointC.setDistanceFromStartKm(4.0);

        List<RoutePoint> route = List.of(
                pointA,
                pointB,
                pointC
        );

        Location passengerLocation = new Location();

            passengerLocation.setType("Point");
            passengerLocation.setCoordinates( new double[] {
                    78.5010,
                    17.4010
            });


        RouteMatchingAlgorithm algorithm =
                new RouteMatchingAlgorithm();

        RouteMatchPoint result =
                algorithm.findNearestRoutePoint(
                        passengerLocation,
                        route
                );

        assertSame(pointB, result.getRoutePoint());
        assertTrue(result.getDeviationKm() < 2.0);
    }
}
