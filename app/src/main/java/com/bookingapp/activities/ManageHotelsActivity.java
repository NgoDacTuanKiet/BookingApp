package com.bookingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bookingapp.adapter.VendorHotelAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Hotel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ManageHotelsActivity extends AppCompatActivity implements VendorHotelAdapter.OnHotelClickListener {

    private RecyclerView rvHotels;
    private FloatingActionButton fabAddHotel;
    private VendorHotelAdapter adapter;
    private AppDatabase db;
    private int vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_hotels);

        db = AppDatabase.getInstance(this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        vendorId = sharedPreferences.getInt("userId", -1);

        rvHotels = findViewById(R.id.rvHotels);
        fabAddHotel = findViewById(R.id.fabAddHotel);

        rvHotels.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VendorHotelAdapter(new ArrayList<>(), this);
        rvHotels.setAdapter(adapter);

        fabAddHotel.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditHotelActivity.class);
            startActivity(intent);
        });

        loadHotels();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHotels();
    }

    private void loadHotels() {
        List<Hotel> hotels = db.hotelDao().getHotelsByVendor(vendorId);
        adapter.setHotels(hotels);
    }

    @Override
    public void onEditClick(Hotel hotel) {
        Intent intent = new Intent(this, AddEditHotelActivity.class);
        intent.putExtra("hotel", hotel);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Hotel hotel) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa khách sạn")
                .setMessage("Bạn có chắc chắn muốn xóa khách sạn này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    db.hotelDao().delete(hotel);
                    loadHotels();
                    Toast.makeText(this, "Đã xóa khách sạn", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
