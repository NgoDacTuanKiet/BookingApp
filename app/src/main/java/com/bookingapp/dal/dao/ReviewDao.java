package com.bookingapp.dal.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.bookingapp.model.Review;
import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insert(Review review);

    @Update
    void update(Review review);

    @Delete
    void delete(Review review);

    // Sắp xếp theo rating giảm dần (ưu tiên đánh giá cao), 
    // nếu bằng rating thì sắp xếp theo date giảm dần (ngày gần nhất)
    @Query("SELECT * FROM reviews WHERE hotelId = :hotelId ORDER BY rating DESC, date DESC")
    List<Review> getReviewsByHotelId(int hotelId);

    @Query("SELECT AVG(rating) FROM reviews WHERE hotelId = :hotelId")
    float getAverageRating(int hotelId);
}
