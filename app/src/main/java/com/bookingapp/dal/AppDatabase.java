package com.bookingapp.dal;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.bookingapp.model.Hotel;
import com.bookingapp.model.User;
import com.bookingapp.model.Review;
import com.bookingapp.dal.dao.HotelDao;
import com.bookingapp.dal.dao.UserDao;
import com.bookingapp.dal.dao.RoomDao;
import com.bookingapp.dal.dao.ReviewDao;

@Database(entities = {User.class, Hotel.class, com.bookingapp.model.Room.class, Review.class}, version = 7, exportSchema = false)
@TypeConverters({DataConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract HotelDao hotelDao();
    public abstract RoomDao roomDao();
    public abstract ReviewDao reviewDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "BookingDB")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
