package com.ryyde.vehicle.service.implementations;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ryyde.common.enums.VehicleType;
import com.ryyde.common.exception.ResourceNotFoundException;
import com.ryyde.vehicle.model.Vehicle;
import com.ryyde.vehicle.repository.VehicleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId("vehicle-1");
        vehicle.setOwnerId("user-1");
        vehicle.setType(VehicleType.CAR);
        vehicle.setBrand("Toyota");
        vehicle.setModel("Innova");
        vehicle.setRegistrationNumber("KA01AB1234");
        vehicle.setSeatCapacity(7);
    }

    @Test
    void createVehicleSavesAndReturnsVehicle() {
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        Vehicle result = vehicleService.createVehicle(vehicle);

        assertSame(vehicle, result);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void getVehicleByIdReturnsVehicleWhenFound() {
        when(vehicleRepository.findById("vehicle-1")).thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.getVehicleById("vehicle-1");

        assertSame(vehicle, result);
    }

    @Test
    void getVehicleByIdThrowsWhenNotFound() {
        when(vehicleRepository.findById("missing")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.getVehicleById("missing"));

        assertEquals("Vehicle not found with id: missing", ex.getMessage());
    }

    @Test
    void getAllVehiclesReturnsAllVehicles() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        List<Vehicle> result = vehicleService.getAllVehicles();

        assertEquals(1, result.size());
        assertSame(vehicle, result.get(0));
    }

    @Test
    void updateVehicleCopiesAllFields() {
        Vehicle existing = new Vehicle();
        existing.setId("vehicle-1");
        existing.setOwnerId("user-1");
        existing.setType(VehicleType.BIKE);
        existing.setBrand("Honda");
        existing.setModel("Activa");
        existing.setRegistrationNumber("KA01XY9999");
        existing.setSeatCapacity(2);

        when(vehicleRepository.findById("vehicle-1")).thenReturn(Optional.of(existing));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        Vehicle result = vehicleService.updateVehicle("vehicle-1", vehicle);

        // ownerId must be preserved, everything else copied from the payload
        assertEquals("user-1", result.getOwnerId());
        assertEquals(VehicleType.CAR, result.getType());
        assertEquals("Toyota", result.getBrand());
        assertEquals("Innova", result.getModel());
        assertEquals("KA01AB1234", result.getRegistrationNumber());
        assertEquals(7, result.getSeatCapacity());
    }

    @Test
    void updateVehicleThrowsWhenNotFound() {
        when(vehicleRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.updateVehicle("missing", new Vehicle()));
    }

    @Test
    void deleteVehicleLoadsThenDeletes() {
        when(vehicleRepository.findById("vehicle-1")).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle("vehicle-1");

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void deleteVehicleThrowsWhenNotFound() {
        when(vehicleRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.deleteVehicle("missing"));
        verify(vehicleRepository, never()).delete(any(Vehicle.class));
    }

    @Test
    void getVehiclesByOwnerFiltersByOwnerId() {
        Vehicle other = new Vehicle();
        other.setId("vehicle-2");
        other.setOwnerId("user-2");

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle, other));

        List<Vehicle> result = vehicleService.getVehiclesByOwner("user-1");

        assertEquals(1, result.size());
        assertSame(vehicle, result.get(0));
    }
}
