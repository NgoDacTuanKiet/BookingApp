package com.bookingapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bookingapp.adapter.RoomAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Hotel;
import com.bookingapp.model.Room;
import com.bumptech.glide.Glide;

import java.util.List;

public class HotelDetailActivity extends AppCompatActivity {

    private Hotel hotel;
    private RecyclerView rvRooms;
    private RoomAdapter roomAdapter;
    private AppDatabase db;
    private Button btnBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        db = AppDatabase.getInstance(this);
        hotel = (Hotel) getIntent().getSerializableExtra("hotel");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvName = findViewById(R.id.detailHotelName);
        TextView tvPrice = findViewById(R.id.detailHotelPrice);
        TextView tvLocation = findViewById(R.id.detailHotelLocation);
        TextView tvDescription = findViewById(R.id.detailDescription);
        ImageView ivHotel = findViewById(R.id.detailHotelImage);

        TextView facWifi = findViewById(R.id.facWifi);
        TextView facPool = findViewById(R.id.facPool);
        TextView facFood = findViewById(R.id.facFood);
        TextView facPark = findViewById(R.id.facPark);

        btnBooking = findViewById(R.id.btnBooking);

        rvRooms = findViewById(R.id.rvRooms);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));

        if (hotel != null) {
            tvName.setText(hotel.name);
            tvPrice.setText("$" + hotel.price + "/Person");
            tvLocation.setText(hotel.city + ", " + hotel.address);
            tvDescription.setText(hotel.description);

            Glide.with(this)
                    .load(hotel.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivHotel);

            facWifi.setAlpha(hotel.hasWifi ? 1.0f : 0.3f);
            facPool.setAlpha(hotel.hasPool ? 1.0f : 0.3f);
            facFood.setAlpha(hotel.hasFoodCourt ? 1.0f : 0.3f);
            facPark.setAlpha(hotel.hasPark ? 1.0f : 0.3f);

            loadRooms();
        }

        // Đơn giản hóa nút đặt phòng, chỉ hiện thông báo đang phát triển
        btnBooking.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đặt phòng đang phát triển.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadRooms() {
        List<Room> rooms = db.roomDao().getRoomsByHotelId(hotel.id);
        roomAdapter = new RoomAdapter(this, rooms);
        rvRooms.setAdapter(roomAdapter);
    }
}
