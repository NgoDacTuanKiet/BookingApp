package com.bookingapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bookingapp.adapter.ManageRoomsHotelAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Hotel;

import java.util.List;

public class ManageRoomsHotelListActivity extends AppCompatActivity {

    private RecyclerView rvHotels;
    private ManageRoomsHotelAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms_hotel_list);

        db = AppDatabase.getInstance(this);
        rvHotels = findViewById(R.id.rvHotels);
        rvHotels.setLayoutManager(new LinearLayoutManager(this));

        loadHotels();
    }

    private void loadHotels() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int vendorId = sharedPreferences.getInt("userId", -1);
        
        List<Hotel> hotels;
        if (vendorId != -1 && vendorId != 999) { // 999 is the hardcoded admin ID from LoginActivity
            hotels = db.hotelDao().getHotelsByVendor(vendorId);
        } else {
            // If it's the super admin (999) or something went wrong, show all for now
            hotels = db.hotelDao().getAll();
        }

        adapter = new ManageRoomsHotelAdapter(this, hotels);
        rvHotels.setAdapter(adapter);
    }
}
