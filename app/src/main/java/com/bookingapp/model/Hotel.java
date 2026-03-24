package com.bookingapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hotels")
public class Hotel {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String address;
    public String city;
    public String description;
}
