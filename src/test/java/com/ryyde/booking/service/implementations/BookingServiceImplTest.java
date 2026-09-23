package com.ryyde.booking.service.implementations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ryyde.booking.model.Booking;
import com.ryyde.booking.repository.BookingRepository;
import com.ryyde.common.enums.BookingStatus;
import com.ryyde.common.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId("booking-1");
        booking.setRideId("ride-1");
        booking.setPassengerId("user-2");
        booking.setSharingCharge(new BigDecimal("150.00"));
    }

    @Test
    void createBookingDefaultsStatusToRequested() {
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(booking);

        assertEquals(BookingStatus.REQUESTED, result.getStatus());
    }

    @Test
    void createBookingStampsCreatedAt() {
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(booking);

        assertNotNull(result.getCreatedAt());
    }

    @Test
    void createBookingKeepsExplicitStatus() {
        booking.setStatus(BookingStatus.ACCEPTED);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(booking);

        assertEquals(BookingStatus.ACCEPTED, result.getStatus());
    }

    @Test
    void getBookingByIdReturnsBookingWhenFound() {
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById("booking-1");

        assertSame(booking, result);
    }

    @Test
    void getBookingByIdThrowsWhenNotFound() {
        when(bookingRepository.findById("missing")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> bookingService.getBookingById("missing"));

        assertEquals("Booking not found with id: missing", ex.getMessage());
    }

    @Test
    void getAllBookingsReturnsAllBookings() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(1, result.size());
        assertSame(booking, result.get(0));
    }

    @Test
    void getBookingsByRideFiltersByRideId() {
        Booking other = new Booking();
        other.setId("booking-2");
        other.setRideId("ride-2");

        when(bookingRepository.findAll()).thenReturn(List.of(booking, other));

        List<Booking> result = bookingService.getBookingsByRide("ride-1");

        assertEquals(1, result.size());
        assertSame(booking, result.get(0));
    }

    @Test
    void getBookingsByPassengerFiltersByPassengerId() {
        Booking other = new Booking();
        other.setId("booking-2");
        other.setPassengerId("user-3");

        when(bookingRepository.findAll()).thenReturn(List.of(booking, other));

        List<Booking> result = bookingService.getBookingsByPassenger("user-2");

        assertEquals(1, result.size());
        assertSame(booking, result.get(0));
    }

    @Test
    void updateBookingStatusSetsNewStatus() {
        booking.setStatus(BookingStatus.REQUESTED);
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.updateBookingStatus("booking-1", BookingStatus.ACCEPTED);

        assertEquals(BookingStatus.ACCEPTED, result.getStatus());
    }

    @Test
    void updateBookingStatusThrowsWhenNotFound() {
        when(bookingRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.updateBookingStatus("missing", BookingStatus.ACCEPTED));
    }

    @Test
    void cancelBookingSetsStatusToCancelled() {
        booking.setStatus(BookingStatus.REQUESTED);
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.cancelBooking("booking-1");

        assertEquals(BookingStatus.CANCELLED, result.getStatus());
    }

    @Test
    void completeBookingSetsStatusToCompleted() {
        booking.setStatus(BookingStatus.ACCEPTED);
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.completeBooking("booking-1");

        assertEquals(BookingStatus.COMPLETED, result.getStatus());
    }

    @Test
    void cancelBookingThrowsWhenNotFound() {
        when(bookingRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.cancelBooking("missing"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
