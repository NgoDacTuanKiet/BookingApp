package com.bookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bookingapp.adapters.RoomAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Room;

import java.util.List;

public class RoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private AppDatabase db;
    private Button btnAddRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        db = AppDatabase.getInstance(this);

        recyclerView = findViewById(R.id.recyclerView);
        btnAddRoom = findViewById(R.id.btnAddRoom);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Room room) {
                Intent intent = new Intent(RoomActivity.this, AddEditRoomActivity.class);
                intent.putExtra("ROOM_ID", room.id);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onDeleteClick(Room room) {
                showDeleteConfirmDialog(room);
            }
        });

        btnAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(RoomActivity.this, AddEditRoomActivity.class);
            startActivityForResult(intent, 1);
        });

        loadRooms();
    }

    private void showDeleteConfirmDialog(Room room) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa phòng này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteRoom(room);
                })
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteRoom(Room room) {
        new Thread(() -> {
            db.roomDao().delete(room);
            runOnUiThread(() -> {
                Toast.makeText(this, "Đã xóa phòng thành công", Toast.LENGTH_SHORT).show();
                loadRooms();
            });
        }).start();
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
        if (resultCode == RESULT_OK) {
            loadRooms();
        }
    }
}
