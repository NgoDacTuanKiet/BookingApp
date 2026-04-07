package com.bookingapp.dal.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Update
    void update(Room room);

    @Delete
    void delete(Room room);

    @Query("SELECT * FROM rooms")
    List<Room> getAllRooms();

    @Query("SELECT * FROM rooms WHERE id = :id")
    Room getRoomById(int id);
}
