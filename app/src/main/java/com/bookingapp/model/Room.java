package com.bookingapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "rooms")
public class Room {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int hotelId;
    public String roomType;
    public double price;
    public int capacity;
    public int quantity;
    public String description;
}