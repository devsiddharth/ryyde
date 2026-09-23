package com.ryyde.ride.service.implementations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ryyde.common.enums.RideStatus;
import com.ryyde.common.exception.ResourceNotFoundException;
import com.ryyde.ride.model.Ride;
import com.ryyde.ride.repository.RideRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideServiceImplTest {

    @Mock
    private RideRepository rideRepository;

    @InjectMocks
    private RideServiceImpl rideService;

    private Ride ride;

    @BeforeEach
    void setUp() {
        ride = new Ride();
        ride.setId("ride-1");
        ride.setDriverId("user-1");
        ride.setVehicleId("vehicle-1");
        ride.setDepartureTime(LocalDateTime.of(2026, 9, 25, 9, 0));
        ride.setDistanceKm(42.5);
        ride.setTotalSeats(4);
        ride.setSharingCharge(new BigDecimal("250.00"));
    }

    @Test
    void createRideDefaultsStatusToScheduled() {
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        Ride result = rideService.createRide(ride);

        assertEquals(RideStatus.SCHEDULED, result.getStatus());
    }

    @Test
    void createRideDefaultsAvailableSeatsToTotalSeats() {
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        Ride result = rideService.createRide(ride);

        assertEquals(4, result.getAvailableSeats());
    }

    @Test
    void createRideStampsCreatedAt() {
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        Ride result = rideService.createRide(ride);

        assertNotNull(result.getCreatedAt());
    }

    @Test
    void createRideKeepsExplicitStatusAndSeats() {
        ride.setStatus(RideStatus.ACTIVE);
        ride.setAvailableSeats(2);
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        Ride result = rideService.createRide(ride);

        assertEquals(RideStatus.ACTIVE, result.getStatus());
        assertEquals(2, result.getAvailableSeats());
    }

    @Test
    void getRideByIdReturnsRideWhenFound() {
        when(rideRepository.findById("ride-1")).thenReturn(Optional.of(ride));

        Ride result = rideService.getRideById("ride-1");

        assertSame(ride, result);
    }

    @Test
    void getRideByIdThrowsWhenNotFound() {
        when(rideRepository.findById("missing")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> rideService.getRideById("missing"));

        assertEquals("Ride not found with id: missing", ex.getMessage());
    }

    @Test
    void getAllRidesReturnsAllRides() {
        when(rideRepository.findAll()).thenReturn(List.of(ride));

        List<Ride> result = rideService.getAllRides();

        assertEquals(1, result.size());
        assertSame(ride, result.get(0));
    }

    @Test
    void updateRideCopiesAllFieldsAndKeepsIdAndDriver() {
        Ride changes = new Ride();
        changes.setVehicleId("vehicle-9");
        changes.setStartLocation(null);
        changes.setEndLocation(null);
        changes.setRoute(List.of());
        changes.setDepartureTime(LocalDateTime.of(2026, 9, 26, 10, 0));
        changes.setEstimatedArrivalTime(LocalDateTime.of(2026, 9, 26, 12, 0));
        changes.setDistanceKm(55.0);
        changes.setTotalSeats(3);
        changes.setSharingCharge(new BigDecimal("300.00"));
        changes.setStatus(RideStatus.SCHEDULED);

        when(rideRepository.findById("ride-1")).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        Ride result = rideService.updateRide("ride-1", changes);

        assertEquals("ride-1", result.getId());
        assertEquals("user-1", result.getDriverId());
        assertEquals("vehicle-9", result.getVehicleId());
        assertEquals(LocalDateTime.of(2026, 9, 26, 10, 0), result.getDepartureTime());
        assertEquals(LocalDateTime.of(2026, 9, 26, 12, 0), result.getEstimatedArrivalTime());
        assertEquals(55.0, result.getDistanceKm());
        assertEquals(3, result.getTotalSeats());
        assertEquals(new BigDecimal("300.00"), result.getSharingCharge());
        assertEquals(RideStatus.SCHEDULED, result.getStatus());
    }

    @Test
    void updateRideThrowsWhenNotFound() {
        when(rideRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> rideService.updateRide("missing", new Ride()));
    }

    @Test
    void cancelRideSetsStatusToCancelled() {
        ride.setStatus(RideStatus.SCHEDULED);
        when(rideRepository.findById("ride-1")).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        Ride result = rideService.cancelRide("ride-1");

        assertEquals(RideStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancelRideThrowsWhenNotFound() {
        when(rideRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rideService.cancelRide("missing"));
    }

    @Test
    void getRidesByDriverFiltersByDriverId() {
        Ride other = new Ride();
        other.setId("ride-2");
        other.setDriverId("user-2");

        when(rideRepository.findAll()).thenReturn(List.of(ride, other));

        List<Ride> result = rideService.getRidesByDriver("user-1");

        assertEquals(1, result.size());
        assertSame(ride, result.get(0));
    }

    @Test
    void getRidesByStatusFiltersByStatus() {
        Ride other = new Ride();
        other.setId("ride-2");
        other.setStatus(RideStatus.ACTIVE);
        ride.setStatus(RideStatus.SCHEDULED);

        when(rideRepository.findAll()).thenReturn(List.of(ride, other));

        List<Ride> result = rideService.getRidesByStatus(RideStatus.SCHEDULED);

        assertEquals(1, result.size());
        assertSame(ride, result.get(0));
    }
}
