package com.bookingapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookings")
public class Booking {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public int roomId;
    public String checkIn;
    public String checkOut;
    public double totalPrice;
    public String status;
}
