package com.bookingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bookingapp.R;

public class VendorMainActivity extends AppCompatActivity {

    Button btnManageHotels, btnManageRooms, btnManageBookings, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_main);

        btnManageHotels = findViewById(R.id.btnManageHotels);
        btnManageRooms = findViewById(R.id.btnManageRooms);
        btnManageBookings = findViewById(R.id.btnManageBookings);
        btnLogout = findViewById(R.id.btnLogout);

        btnManageHotels.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageHotelsActivity.class);
            startActivity(intent);
        });

        btnManageRooms.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageRoomsHotelListActivity.class);
            startActivity(intent);
        });

        btnManageBookings.setOnClickListener(v -> {
            // Intent intent = new Intent(this, ManageBookingsActivity.class);
            // startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            
            Intent intent = new Intent(VendorMainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
