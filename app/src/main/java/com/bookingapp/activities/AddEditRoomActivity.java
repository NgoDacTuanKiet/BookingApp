package com.bookingapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bookingapp.R;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Room;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditRoomActivity extends AppCompatActivity {

    private TextInputEditText etHotelId, etRoomType, etPrice, etCapacity, etQuantity, etDescription, etImageUrl;
    private Button btnSave;
    private TextView tvTitle;
    private AppDatabase db;
    private Room currentRoom;
    private boolean isEditMode = false;
    private int hotelIdFromIntent = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_room);

        db = AppDatabase.getInstance(this);

        tvTitle = findViewById(R.id.tvTitle);
        etHotelId = findViewById(R.id.etHotelId);
        etRoomType = findViewById(R.id.etRoomType);
        etPrice = findViewById(R.id.etPrice);
        etCapacity = findViewById(R.id.etCapacity);
        etQuantity = findViewById(R.id.etQuantity);
        etDescription = findViewById(R.id.etDescription);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnSave = findViewById(R.id.btnSave);

        int roomId = getIntent().getIntExtra("ROOM_ID", -1);
        hotelIdFromIntent = getIntent().getIntExtra("HOTEL_ID", -1);

        if (roomId != -1) {
            isEditMode = true;
            tvTitle.setText("Sửa thông tin phòng");
            loadRoomData(roomId);
        } else {
            isEditMode = false;
            tvTitle.setText("Thêm phòng mới");
            if (hotelIdFromIntent != -1) {
                etHotelId.setText(String.valueOf(hotelIdFromIntent));
                etHotelId.setEnabled(false); // Disable editing hotel ID when adding from a specific hotel
            }
        }

        btnSave.setOnClickListener(v -> saveRoom());
    }

    private void loadRoomData(int roomId) {
        new Thread(() -> {
            currentRoom = db.roomDao().getRoomById(roomId);
            runOnUiThread(() -> {
                if (currentRoom != null) {
                    etHotelId.setText(String.valueOf(currentRoom.hotelId));
                    etHotelId.setEnabled(false);
                    etRoomType.setText(currentRoom.roomType);
                    etPrice.setText(String.valueOf(currentRoom.price));
                    etCapacity.setText(String.valueOf(currentRoom.capacity));
                    etQuantity.setText(String.valueOf(currentRoom.quantity));
                    etDescription.setText(currentRoom.description);
                    etImageUrl.setText(currentRoom.imageUrl);
                }
            });
        }).start();
    }

    private void saveRoom() {
        String hotelIdStr = etHotelId.getText().toString();
        String roomType = etRoomType.getText().toString();
        String priceStr = etPrice.getText().toString();
        String capacityStr = etCapacity.getText().toString();
        String quantityStr = etQuantity.getText().toString();
        String description = etDescription.getText().toString();
        String imageUrl = etImageUrl.getText().toString();

        if (hotelIdStr.isEmpty() || roomType.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEditMode) {
            currentRoom = new Room();
        }

        currentRoom.hotelId = Integer.parseInt(hotelIdStr);
        currentRoom.roomType = roomType;
        currentRoom.price = Double.parseDouble(priceStr);
        currentRoom.capacity = Integer.parseInt(capacityStr);
        currentRoom.quantity = Integer.parseInt(quantityStr);
        currentRoom.description = description;
        currentRoom.imageUrl = imageUrl;

        new Thread(() -> {
            if (isEditMode) {
                db.roomDao().update(currentRoom);
            } else {
                db.roomDao().insert(currentRoom);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
