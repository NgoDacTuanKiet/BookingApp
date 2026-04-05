package com.bookingapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "hotels")
public class Hotel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String address;
    public String city;
    public String description;
    public double rating;
    public String imageUrl;
    public double price;
    public int bedrooms;
    public int bathrooms;
    public int guestrooms;
    public boolean hasWifi;
    public boolean hasPool;
    public boolean hasFoodCourt;
    public boolean hasPark;

    public Hotel() {}

    public Hotel(String name, String address, String city, String description, double rating, String imageUrl, double price, int bedrooms, int bathrooms, int guestrooms, boolean hasWifi, boolean hasPool, boolean hasFoodCourt, boolean hasPark) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.description = description;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.price = price;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.guestrooms = guestrooms;
        this.hasWifi = hasWifi;
        this.hasPool = hasPool;
        this.hasFoodCourt = hasFoodCourt;
        this.hasPark = hasPark;
    }
}
