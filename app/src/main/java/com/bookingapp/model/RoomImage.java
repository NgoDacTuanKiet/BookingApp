package com.bookingapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "room_images")
public class RoomImage {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int roomId;
    public String imageUrl;
}
