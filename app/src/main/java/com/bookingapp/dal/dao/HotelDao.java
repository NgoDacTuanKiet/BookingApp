package com.bookingapp.dal.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.bookingapp.model.Hotel;
import java.util.List;

@Dao
public interface HotelDao {
    @Query("SELECT * FROM hotels")
    List<Hotel> getAll();

    @Query("SELECT * FROM hotels ORDER BY rating DESC LIMIT 10")
    List<Hotel> getTopRated();

    @Query("SELECT * FROM hotels WHERE name LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%'")
    List<Hotel> searchHotels(String query);

    @Query("SELECT * FROM hotels WHERE " +
            "(name LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%') " +
            "AND price BETWEEN :minPrice AND :maxPrice " +
            "AND rating >= :minRating " +
            "AND (:hasWifi = 0 OR hasWifi = :hasWifi) " +
            "AND (:hasPool = 0 OR hasPool = :hasPool) " +
            "AND (:hasFoodCourt = 0 OR hasFoodCourt = :hasFoodCourt) " +
            "AND (:hasPark = 0 OR hasPark = :hasPark)")
    List<Hotel> advancedSearch(String query, double minPrice, double maxPrice, double minRating, int hasWifi, int hasPool, int hasFoodCourt, int hasPark);

    @Query("SELECT * FROM hotels WHERE vendorId = :vendorId")
    List<Hotel> getHotelsByVendor(int vendorId);

    @Insert
    long insert(Hotel hotel);

    @Insert
    void insertAll(Hotel... hotels);

    @Update
    void update(Hotel hotel);

    @Delete
    void delete(Hotel hotel);
}
