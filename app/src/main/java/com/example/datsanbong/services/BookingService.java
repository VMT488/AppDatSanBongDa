package com.example.datsanbong.services;

import com.example.datsanbong.models.Booking;
import com.google.firebase.database.DatabaseReference;

public class BookingService {
    private final DatabaseReference reference =
            FirebaseRealtimeService.getReference("Bookings");

    public void addBooking(Booking booking){

        reference.push().setValue(booking);

    }

}
