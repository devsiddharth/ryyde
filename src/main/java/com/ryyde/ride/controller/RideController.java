package com.ryyde.ride.controller;

import com.ryyde.ride.model.Ride;
import com.ryyde.ride.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides/")
@RequiredArgsConstructor

public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<Ride> createRide(@RequestBody Ride ride){
        Ride createdRide = rideService.createRide(ride);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ride);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable String id){
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @GetMapping
    public ResponseEntity<List<Ride>> getAllRides(){
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Ride> updateRideById(@PathVariable String id, @RequestBody Ride ride){
        return ResponseEntity.ok(rideService.updateRide(id, ride));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelRideById(@PathVariable String id){
        rideService.cancelRide(id);
        return ResponseEntity.noContent().build();
    }


}
