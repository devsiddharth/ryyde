package com.ryyde.vehicle.controller;

import com.ryyde.vehicle.model.Vehicle;
import com.ryyde.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles/")
@RequiredArgsConstructor

public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle){
        Vehicle createdVehicle = vehicleService.createVehicle(vehicle);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdVehicle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id){
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Vehicle>> getVehiclesByOwnerId(@PathVariable String ownerId){
        return ResponseEntity.ok(vehicleService.getVehiclesByOwner(ownerId));
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles(){
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicleById(@PathVariable String id, @RequestBody Vehicle vehicle){
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicleById(@PathVariable String id){
        vehicleService.deleteVehicle(id);

        return ResponseEntity.noContent().build();
    }
}
