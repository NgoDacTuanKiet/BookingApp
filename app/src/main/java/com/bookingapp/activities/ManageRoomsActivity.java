package com.bookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bookingapp.adapter.ManageRoomAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ManageRoomsActivity extends AppCompatActivity implements ManageRoomAdapter.OnRoomDeletedListener {

    private RecyclerView rvRooms;
    private FloatingActionButton fabAddRoom;
    private TextView tvTitle;
    private ManageRoomAdapter adapter;
    private AppDatabase db;
    private int hotelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityroom);

        db = AppDatabase.getInstance(this);
        rvRooms = findViewById(R.id.rvRooms);
        fabAddRoom = findViewById(R.id.fabAddRoom);
        tvTitle = findViewById(R.id.tvTitle);

        hotelId = getIntent().getIntExtra("HOTEL_ID", -1);
        String hotelName = getIntent().getStringExtra("HOTEL_NAME");
        
        if (hotelName != null) {
            tvTitle.setText("Phòng của: " + hotelName);
        }

        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        loadRooms();

        fabAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(ManageRoomsActivity.this, AddEditRoomActivity.class);
            intent.putExtra("HOTEL_ID", hotelId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
    }

    private void loadRooms() {
        List<Room> rooms;
        if (hotelId != -1) {
            rooms = db.roomDao().getRoomsByHotelId(hotelId);
        } else {
            rooms = db.roomDao().getAllRooms();
        }
        adapter = new ManageRoomAdapter(this, rooms, this);
        rvRooms.setAdapter(adapter);
    }

    @Override
    public void onRoomDeleted() {
        loadRooms();
    }
}
