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

}
