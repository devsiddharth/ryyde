package com.ryyde.rideRequest.service.implementations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ryyde.common.enums.RideRequestStatus;
import com.ryyde.common.exception.ResourceNotFoundException;
import com.ryyde.rideRequest.model.RideRequest;
import com.ryyde.rideRequest.repository.RideRequestRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideRequestServiceImplTest {

    @Mock
    private RideRequestRepository rideRequestRepository;

    @InjectMocks
    private RideRequestServiceImpl rideRequestService;

    private RideRequest rideRequest;

    @BeforeEach
    void setUp() {
        rideRequest = new RideRequest();
        rideRequest.setId("request-1");
        rideRequest.setRideId("ride-1");
        rideRequest.setPassengerId("user-2");
        rideRequest.setEstimatedCharge(new BigDecimal("120.00"));
    }

    @Test
    void createRequestDefaultsStatusToPending() {
        when(rideRequestRepository.save(any(RideRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        RideRequest result = rideRequestService.createRequest(rideRequest);

        assertEquals(RideRequestStatus.PENDING, result.getStatus());
    }

    @Test
    void createRequestStampsCreatedAt() {
        when(rideRequestRepository.save(any(RideRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        RideRequest result = rideRequestService.createRequest(rideRequest);

        assertNotNull(result.getCreatedAt());
    }

    @Test
    void createRequestKeepsExplicitStatus() {
        rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        when(rideRequestRepository.save(any(RideRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        RideRequest result = rideRequestService.createRequest(rideRequest);

        assertEquals(RideRequestStatus.ACCEPTED, result.getStatus());
    }

    @Test
    void getRequestByIdReturnsRequestWhenFound() {
        when(rideRequestRepository.findById("request-1")).thenReturn(Optional.of(rideRequest));

        RideRequest result = rideRequestService.getRequestById("request-1");

        assertSame(rideRequest, result);
    }

    @Test
    void getRequestByIdThrowsWhenNotFound() {
        when(rideRequestRepository.findById("missing")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> rideRequestService.getRequestById("missing"));

        assertEquals("Ride request not found with id: missing", ex.getMessage());
    }

    @Test
    void getAllRequestsReturnsAllRequests() {
        when(rideRequestRepository.findAll()).thenReturn(List.of(rideRequest));

        List<RideRequest> result = rideRequestService.getAllRequests();

        assertEquals(1, result.size());
        assertSame(rideRequest, result.get(0));
    }

    @Test
    void getRequestsByRideFiltersByRideId() {
        RideRequest other = new RideRequest();
        other.setId("request-2");
        other.setRideId("ride-2");

        when(rideRequestRepository.findAll()).thenReturn(List.of(rideRequest, other));

        List<RideRequest> result = rideRequestService.getRequestsByRide("ride-1");

        assertEquals(1, result.size());
        assertSame(rideRequest, result.get(0));
    }

    @Test
    void getRequestsByPassengerFiltersByPassengerId() {
        RideRequest other = new RideRequest();
        other.setId("request-2");
        other.setPassengerId("user-3");

        when(rideRequestRepository.findAll()).thenReturn(List.of(rideRequest, other));

        List<RideRequest> result = rideRequestService.getRequestsByPassenger("user-2");

        assertEquals(1, result.size());
        assertSame(rideRequest, result.get(0));
    }

    @Test
    void getRequestsByStatusFiltersByStatus() {
        RideRequest other = new RideRequest();
        other.setId("request-2");
        other.setStatus(RideRequestStatus.ACCEPTED);
        rideRequest.setStatus(RideRequestStatus.PENDING);

        when(rideRequestRepository.findAll()).thenReturn(List.of(rideRequest, other));

        List<RideRequest> result = rideRequestService.getRequestsByStatus(RideRequestStatus.PENDING);

        assertEquals(1, result.size());
        assertSame(rideRequest, result.get(0));
    }

    @Test
    void updateRequestStatusSetsNewStatus() {
        rideRequest.setStatus(RideRequestStatus.PENDING);
        when(rideRequestRepository.findById("request-1")).thenReturn(Optional.of(rideRequest));
        when(rideRequestRepository.save(any(RideRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        RideRequest result = rideRequestService.updateRequestStatus("request-1", RideRequestStatus.ACCEPTED);

        assertEquals(RideRequestStatus.ACCEPTED, result.getStatus());
    }

    @Test
    void updateRequestStatusThrowsWhenNotFound() {
        when(rideRequestRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> rideRequestService.updateRequestStatus("missing", RideRequestStatus.ACCEPTED));
    }

    @Test
    void cancelRequestSetsStatusToCancelled() {
        rideRequest.setStatus(RideRequestStatus.PENDING);
        when(rideRequestRepository.findById("request-1")).thenReturn(Optional.of(rideRequest));
        when(rideRequestRepository.save(any(RideRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        RideRequest result = rideRequestService.cancelRequest("request-1");

        assertEquals(RideRequestStatus.CANCELLED, result.getStatus());
        verify(rideRequestRepository).save(rideRequest);
    }

    @Test
    void cancelRequestThrowsWhenNotFound() {
        when(rideRequestRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rideRequestService.cancelRequest("missing"));
        verify(rideRequestRepository, never()).save(any(RideRequest.class));
    }
}
