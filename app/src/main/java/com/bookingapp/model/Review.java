package com.bookingapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "reviews",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Hotel.class,
                        parentColumns = "id",
                        childColumns = "hotelId",
                        onDelete = ForeignKey.CASCADE)
        })
public class Review implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public int hotelId;
    public String userName;
    public float rating;
    public String comment;
    public String date;

    public Review() {}

    public Review(int userId, int hotelId, String userName, float rating, String comment, String date) {
        this.userId = userId;
        this.hotelId = hotelId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }
}
