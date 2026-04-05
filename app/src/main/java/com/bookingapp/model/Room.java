package com.bookingapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bookingapp.dal.DataConverter;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "rooms")
public class Room implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int hotelId;
    public String roomType;
    public double price;
    public int capacity;
    public int quantity;
    public String description;
    public String imageUrl;

    @TypeConverters(DataConverter.class)
    public List<String> images; // Danh sách nhiều ảnh của phòng
}