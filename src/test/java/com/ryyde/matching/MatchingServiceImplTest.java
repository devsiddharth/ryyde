package com.ryyde.matching;

import com.ryyde.common.enums.RideStatus;
import com.ryyde.matching.algorithm.RouteMatchPoint;
import com.ryyde.matching.algorithm.RouteMatchingAlgorithm;
import com.ryyde.matching.service.impl.MatchingServiceImpl;
import com.ryyde.ride.model.Location;
import com.ryyde.ride.model.Ride;
import com.ryyde.ride.model.RoutePoint;
import com.ryyde.ride.repository.RideRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.ryyde.common.enums.RideStatus.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchingServiceImplTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RouteMatchingAlgorithm algorithm;

    @InjectMocks
    private MatchingServiceImpl matchingService;

    @Test
    void shouldReturnMatchingRide() {

        // Arrange

        Ride ride = new Ride();
        ride.setStatus(ACTIVE);
        ride.setAvailableSeats(3);

        Location pickupLocation = new Location();
        pickupLocation.setType("Point");
        pickupLocation.setCoordinates(
                new double[]{78.5010, 17.4010}
        );

        Location dropLocation = new Location();
        dropLocation.setType("Point");
        dropLocation.setCoordinates(
                new double[]{78.5150, 17.4150}
        );

        RoutePoint pickupPoint = new RoutePoint();
        pickupPoint.setDistanceFromStartKm(5.0);

        RoutePoint dropPoint = new RoutePoint();
        dropPoint.setDistanceFromStartKm(15.0);

        RouteMatchPoint pickupMatch =
                new RouteMatchPoint(
                        pickupPoint,
                        0.5
                );

        RouteMatchPoint dropMatch =
                new RouteMatchPoint(
                        dropPoint,
                        0.8
                );

        when(rideRepository.findAll())
                .thenReturn(List.of(ride));

        when(algorithm.findNearestRoutePoint(
                pickupLocation,
                ride.getRoute()
        )).thenReturn(pickupMatch);

        when(algorithm.findNearestRoutePoint(
                dropLocation,
                ride.getRoute()
        )).thenReturn(dropMatch);


        // Act

        List<Ride> result =
                matchingService.findMatchingRides(
                        pickupLocation,
                        dropLocation
                );


        // Assert

        assertEquals(1, result.size());
        assertSame(ride, result.get(0));
    }

    @Test
    void shouldRejectInactiveRide() {

        // Arrange
        Ride ride = new Ride();
        ride.setStatus(RideStatus.COMPLETED);
        ride.setAvailableSeats(3);

        when(rideRepository.findAll())
                .thenReturn(List.of(ride));

        // Act
        List<Ride> result = matchingService.findMatchingRides(
                new Location(),
                new Location()
        );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRejectRideIfNoAvailableSeats() {

        // Arrange
        Ride ride = new Ride();
        ride.setStatus(ACTIVE);
        ride.setAvailableSeats(0);

        when(rideRepository.findAll())
                .thenReturn(List.of(ride));

        // Act
        List<Ride> result = matchingService.findMatchingRides(
                new Location(),
                new Location()
        );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRejectRideWhenDropComesBeforePickup() {

        // Arrange
        Ride ride = new Ride();
        ride.setStatus(RideStatus.ACTIVE);
        ride.setAvailableSeats(3);

        RoutePoint pickupPoint = new RoutePoint();
        pickupPoint.setDistanceFromStartKm(15.0);

        RoutePoint dropPoint = new RoutePoint();
        dropPoint.setDistanceFromStartKm(5.0);

        RouteMatchPoint pickupMatch = new RouteMatchPoint(pickupPoint, 0.5);
        RouteMatchPoint dropMatch = new RouteMatchPoint(dropPoint, 0.8);

        Location pickupLocation = new Location();
        Location dropLocation = new Location();

        when(rideRepository.findAll())
                .thenReturn(List.of(ride));

        when(algorithm.findNearestRoutePoint(pickupLocation, ride.getRoute()))
                .thenReturn(pickupMatch);

        when(algorithm.findNearestRoutePoint(dropLocation, ride.getRoute()))
                .thenReturn(dropMatch);

        // Act
        List<Ride> result = matchingService.findMatchingRides(
                pickupLocation,
                dropLocation
        );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRejectRideWhenPickupIsTooFarFromRoute(){
        // Arrange
        Ride ride = new Ride();
        ride.setStatus(RideStatus.ACTIVE);
        ride.setAvailableSeats(3);

        RoutePoint pickupPoint = new RoutePoint();
        pickupPoint.setDistanceFromStartKm(5.0);

        RoutePoint dropPoint = new RoutePoint();
        dropPoint.setDistanceFromStartKm(15.0);

        RouteMatchPoint pickupMatch = new RouteMatchPoint(pickupPoint, 2.5);
        RouteMatchPoint dropMatch = new RouteMatchPoint(dropPoint, 0.8);

        Location pickupLocation = new Location();
        Location dropLocation = new Location();

        when(rideRepository.findAll())
                .thenReturn(List.of(ride));

        when(algorithm.findNearestRoutePoint(pickupLocation, ride.getRoute()))
                .thenReturn(pickupMatch);

        when(algorithm.findNearestRoutePoint(dropLocation, ride.getRoute()))
                .thenReturn(dropMatch);

        // Act
        List<Ride> result = matchingService.findMatchingRides(
                pickupLocation,
                dropLocation
        );

        // Assert
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldRejectRideWhenDropIsTooFarFromRoute(){
        // Arrange
        Ride ride = new Ride();
        ride.setStatus(RideStatus.ACTIVE);
        ride.setAvailableSeats(3);

        RoutePoint pickupPoint = new RoutePoint();
        pickupPoint.setDistanceFromStartKm(5.0);

        RoutePoint dropPoint = new RoutePoint();
        dropPoint.setDistanceFromStartKm(15.0);

        RouteMatchPoint pickupMatch = new RouteMatchPoint(pickupPoint, 0.5);
        RouteMatchPoint dropMatch = new RouteMatchPoint(dropPoint, 2.8);

        Location pickupLocation = new Location();
        Location dropLocation = new Location();

        when(rideRepository.findAll())
                .thenReturn(List.of(ride));

        when(algorithm.findNearestRoutePoint(pickupLocation, ride.getRoute()))
                .thenReturn(pickupMatch);

        when(algorithm.findNearestRoutePoint(dropLocation, ride.getRoute()))
                .thenReturn(dropMatch);

        // Act
        List<Ride> result = matchingService.findMatchingRides(
                pickupLocation,
                dropLocation
        );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnMultipleMatchedRides() {

        // Arrange
        Ride ride1 = new Ride();
        ride1.setStatus(RideStatus.ACTIVE);
        ride1.setAvailableSeats(1);

        Ride ride2 = new Ride();
        ride2.setStatus(RideStatus.ACTIVE);
        ride2.setAvailableSeats(3);

        RoutePoint pickupPoint1 = new RoutePoint();
        pickupPoint1.setDistanceFromStartKm(5.0);

        RoutePoint dropPoint1 = new RoutePoint();
        dropPoint1.setDistanceFromStartKm(15.0);

        RoutePoint pickupPoint2 = new RoutePoint();
        pickupPoint2.setDistanceFromStartKm(5.0);

        RoutePoint dropPoint2 = new RoutePoint();
        dropPoint2.setDistanceFromStartKm(15.0);

        List<RoutePoint> route1 = List.of(pickupPoint1, dropPoint1);
        List<RoutePoint> route2 = List.of(pickupPoint2, dropPoint2);

        ride1.setRoute(route1);
        ride2.setRoute(route2);

        Location pickupLocation = new Location();
        pickupLocation.setType("Point");
        pickupLocation.setCoordinates(
                new double[]{78.5010, 17.4010}
        );

        Location dropLocation = new Location();
        dropLocation.setType("Point");
        dropLocation.setCoordinates(
                new double[]{78.5150, 17.4150}
        );

        RouteMatchPoint pickupMatch1 =
                new RouteMatchPoint(pickupPoint1, 1.8);
        RouteMatchPoint dropMatch1 =
                new RouteMatchPoint(dropPoint1, 0.8);

        RouteMatchPoint pickupMatch2 =
                new RouteMatchPoint(pickupPoint2, 0.5);
        RouteMatchPoint dropMatch2 =
                new RouteMatchPoint(dropPoint2, 1.9);

        when(rideRepository.findAll())
                .thenReturn(List.of(ride1, ride2));

        when(algorithm.findNearestRoutePoint(pickupLocation, route1))
                .thenReturn(pickupMatch1);

        when(algorithm.findNearestRoutePoint(dropLocation, route1))
                .thenReturn(dropMatch1);

        when(algorithm.findNearestRoutePoint(pickupLocation, route2))
                .thenReturn(pickupMatch2);

        when(algorithm.findNearestRoutePoint(dropLocation, route2))
                .thenReturn(dropMatch2);

        // Act
        List<Ride> result = matchingService.findMatchingRides(
                pickupLocation,
                dropLocation
        );

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(ride1));
        assertTrue(result.contains(ride2));
    }



}
