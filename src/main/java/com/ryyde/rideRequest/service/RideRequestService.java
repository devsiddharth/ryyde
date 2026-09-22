package com.ryyde.rideRequest.service;

import java.util.List;

import com.ryyde.common.enums.RideRequestStatus;
import com.ryyde.rideRequest.model.RideRequest;

public interface RideRequestService {

    RideRequest createRequest(RideRequest rideRequest);

    RideRequest getRequestById(String id);

    List<RideRequest> getAllRequests();

    List<RideRequest> getRequestsByRide(String rideId);

    List<RideRequest> getRequestsByPassenger(String passengerId);

    List<RideRequest> getRequestsByStatus(RideRequestStatus status);

    RideRequest updateRequestStatus(String id, RideRequestStatus status);

    RideRequest cancelRequest(String id);
}
