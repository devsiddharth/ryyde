package com.ryyde.booking.controller;

import com.ryyde.booking.model.Booking;
import com.ryyde.booking.service.BookingService;
import com.ryyde.common.enums.BookingStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking){
        return ResponseEntity.ok(bookingService.createBooking(booking));
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(){
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable String id){
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<List<Booking>> getBookingsByRide(@PathVariable String rideId){
        return  ResponseEntity.ok(bookingService.getBookingsByRide(rideId));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Booking>> getBookingsByPassenger(@PathVariable String passengerId){
        return  ResponseEntity.ok(bookingService.getBookingsByPassenger(passengerId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable String id, @RequestParam BookingStatus status){
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBookingStatus(@PathVariable String id){
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PatchMapping("/{id}/complete")
    public  ResponseEntity<Booking> completeBookingStatus(@PathVariable String id){
        return  ResponseEntity.ok(bookingService.completeBooking(id));
    }

}
