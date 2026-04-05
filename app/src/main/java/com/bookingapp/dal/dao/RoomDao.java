package com.bookingapp.dal.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.bookingapp.model.Room;

import java.util.List;

@Dao
public interface RoomDao {
    @Query("SELECT * FROM rooms WHERE hotelId = :hotelId")
    List<Room> getRoomsByHotelId(int hotelId);

    @Insert
    void insert(Room room);

    @Insert
    void insertAll(Room... rooms);

    @Query("SELECT * FROM rooms")
    List<Room> getAllRooms();
}
