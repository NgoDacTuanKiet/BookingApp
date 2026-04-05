package com.bookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.activities.AddEditRoomActivity;
import com.bookingapp.activities.DeleteRoomActivity;
import com.bookingapp.adapters.RoomAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Room;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_REFRESH = 1;
    private Button btnAddRoom;
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // Initialize Database
        db = AppDatabase.getInstance(this);

        // Initialize UI components
        btnAddRoom = findViewById(R.id.btnAddRoom);
        recyclerView = findViewById(R.id.recyclerView);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomAdapter();
        recyclerView.setAdapter(adapter);

        // Load data
        loadRooms();

        // Handle Add button
        btnAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditRoomActivity.class);
            startActivityForResult(intent, REQUEST_CODE_REFRESH);
        });

        // Handle Adapter clicks
        adapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Room room) {
                Intent intent = new Intent(MainActivity.this, AddEditRoomActivity.class);
                intent.putExtra("ROOM_ID", room.id);
                startActivityForResult(intent, REQUEST_CODE_REFRESH);
            }

            @Override
            public void onDeleteClick(Room room) {
                Intent intent = new Intent(MainActivity.this, DeleteRoomActivity.class);
                intent.putExtra("ROOM_ID", room.id);
                startActivityForResult(intent, REQUEST_CODE_REFRESH);
            }
        });
    }

    private void loadRooms() {
        new Thread(() -> {
            List<Room> rooms = db.roomDao().getAllRooms();
            runOnUiThread(() -> adapter.setRooms(rooms));
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REFRESH && resultCode == RESULT_OK) {
            loadRooms();
        }
    }
}
