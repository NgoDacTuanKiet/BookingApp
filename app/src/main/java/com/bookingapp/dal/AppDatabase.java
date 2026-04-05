package com.bookingapp.dal;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.bookingapp.model.User;
import com.bookingapp.dal.dao.UserDao;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}