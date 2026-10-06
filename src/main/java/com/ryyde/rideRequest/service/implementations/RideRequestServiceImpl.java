package com.ryyde.rideRequest.service.implementations;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ryyde.common.enums.RideRequestStatus;
import com.ryyde.common.exception.ResourceNotFoundException;
import com.ryyde.rideRequest.model.RideRequest;
import com.ryyde.rideRequest.repository.RideRequestRepository;
import com.ryyde.rideRequest.service.RideRequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequest createRequest(RideRequest rideRequest) {
        if (rideRequest.getStatus() == null) {
            rideRequest.setStatus(RideRequestStatus.PENDING);
        }
        if (rideRequest.getCreatedAt() == null) {
            rideRequest.setCreatedAt(Instant.now());
        }
        return rideRequestRepository.save(rideRequest);
    }

    @Override
    public RideRequest getRequestById(String id) {
        return rideRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride request not found with id: " + id));
    }

    @Override
    public List<RideRequest> getAllRequests() {
        return rideRequestRepository.findAll();
    }

    @Override
    public List<RideRequest> getRequestsByRide(String rideId) {
        return rideRequestRepository.findAll().stream()
                .filter(request -> rideId.equals(request.getRideId()))
                .toList();
    }

    @Override
    public List<RideRequest> getRequestsByPassenger(String passengerId) {
        return rideRequestRepository.findAll().stream()
                .filter(request -> passengerId.equals(request.getPassengerId()))
                .toList();
    }

    @Override
    public List<RideRequest> getRequestsByStatus(RideRequestStatus status) {
        return rideRequestRepository.findAll().stream()
                .filter(request -> status == request.getStatus())
                .toList();
    }

    @Override
    public RideRequest updateRequestStatus(String id, RideRequestStatus status){
        RideRequest existingRideRequest = getRequestById(id);

        existingRideRequest.setStatus(status);

        return rideRequestRepository.save(existingRideRequest);
    }

    @Override
    public RideRequest cancelRequest(String id) {
        return updateRequestStatus(id, RideRequestStatus.CANCELLED);
    }
}