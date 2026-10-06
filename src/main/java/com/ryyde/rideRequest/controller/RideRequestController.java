package com.ryyde.rideRequest.controller;

import com.ryyde.common.enums.RideRequestStatus;
import com.ryyde.rideRequest.model.RideRequest;
import com.ryyde.rideRequest.service.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ride-requests/")
@RequiredArgsConstructor

public class RideRequestController {

    private final RideRequestService rideRequestService;

    @PostMapping
    public ResponseEntity<RideRequest> createRideRequest(@RequestBody RideRequest rideRequest){
        RideRequest createdRideRequest = rideRequestService.createRequest(rideRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(rideRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideRequest> getRideRequestById(@PathVariable String id){
        return ResponseEntity.ok(rideRequestService.getRequestById(id));
    }

    @GetMapping
    public ResponseEntity<List<RideRequest>> getAllRideRequests(){
        return ResponseEntity.ok(rideRequestService.getAllRequests());
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<List<RideRequest>> getRequestsByRideId(@PathVariable String rideId){
        return ResponseEntity.ok(rideRequestService.getRequestsByRide(rideId));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<RideRequest>> getRequestsByPassengerId(@PathVariable String passengerId){
        return ResponseEntity.ok(rideRequestService.getRequestsByRide(passengerId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RideRequest>> getRequestsByStatus(@PathVariable RideRequestStatus status){
        return ResponseEntity.ok(rideRequestService.getRequestsByStatus(status));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RideRequest> updateRideRequestById(@PathVariable String id, @RequestBody RideRequestStatus rideRequestStatus){
        return ResponseEntity.ok(rideRequestService.updateRequestStatus(id,rideRequestStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> canceRideRequestById(@PathVariable String id){
        rideRequestService.cancelRequest(id);

        return ResponseEntity.noContent().build();
    }


}
