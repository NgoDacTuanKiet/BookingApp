package com.bookingapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bookingapp.R;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Room;

import java.util.Arrays;
import java.util.List;

public class AddEditRoomActivity extends AppCompatActivity {

    private Spinner spRoomType;
    private EditText etPrice, etCapacity, etQuantity, etDescription;
    private Button btnSave;
    private ImageButton btnBack;
    private TextView tvTitle;
    private AppDatabase db;
    private Room roomToEdit = null;
    
    private final List<String> roomTypes = Arrays.asList("Standard", "Deluxe", "Suite", "Family");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_room);

        db = AppDatabase.getInstance((Context) this);

        spRoomType = findViewById(R.id.spRoomType);
        etPrice = findViewById(R.id.etPrice);
        etCapacity = findViewById(R.id.etCapacity);
        etQuantity = findViewById(R.id.etQuantity);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, roomTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoomType.setAdapter(adapter);

        // Check if we are editing
        if (getIntent().hasExtra("ROOM_ID")) {
            int roomId = getIntent().getIntExtra("ROOM_ID", -1);
            new Thread(() -> {
                roomToEdit = db.roomDao().getRoomById(roomId);
                runOnUiThread(() -> {
                    if (roomToEdit != null) {
                        int selectionIndex = roomTypes.indexOf(roomToEdit.roomType);
                        if (selectionIndex >= 0) {
                            spRoomType.setSelection(selectionIndex);
                        }
                        etPrice.setText(String.valueOf(roomToEdit.price));
                        etCapacity.setText(String.valueOf(roomToEdit.capacity));
                        etQuantity.setText(String.valueOf(roomToEdit.quantity));
                        etDescription.setText(roomToEdit.description);
                        btnSave.setText("Cập nhật");
                        tvTitle.setText("Chỉnh sửa phòng");
                    }
                });
            }).start();
        } else {
            tvTitle.setText("Thêm phòng mới");
        }

        btnSave.setOnClickListener(v -> saveRoom());
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveRoom() {
        String type = spRoomType.getSelectedItem().toString();
        String priceStr = etPrice.getText().toString().trim();
        String capacityStr = etCapacity.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        // 1. Kiểm tra các trường trống
        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Giá phòng không được để trống");
            etPrice.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(capacityStr)) {
            etCapacity.setError("Sức chứa không được để trống");
            etCapacity.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(quantityStr)) {
            etQuantity.setError("Số lượng không được để trống");
            etQuantity.requestFocus();
            return;
        }

        try {
            // 2. Kiểm tra giá phòng
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etPrice.setError("Giá phòng phải lớn hơn 0");
                etPrice.requestFocus();
                return;
            }
            if (price > 1000000) { // Giới hạn thực tế ví dụ
                etPrice.setError("Giá phòng không hợp lý (quá lớn)");
                etPrice.requestFocus();
                return;
            }

            // 3. Kiểm tra sức chứa
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                etCapacity.setError("Sức chứa tối thiểu là 1 người");
                etCapacity.requestFocus();
                return;
            }
            if (capacity > 50) { // Giới hạn cho phòng lớn nhất
                etCapacity.setError("Sức chứa không vượt quá 50 người");
                etCapacity.requestFocus();
                return;
            }

            // 4. Kiểm tra số lượng phòng
            int quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                etQuantity.setError("Số lượng phòng không được là số âm");
                etQuantity.requestFocus();
                return;
            }
            if (quantity > 200) {
                etQuantity.setError("Số lượng phòng quá lớn (tối đa 200)");
                etQuantity.requestFocus();
                return;
            }

            // 5. Kiểm tra độ dài mô tả (nếu có)
            if (desc.length() > 500) {
                etDescription.setError("Mô tả không được vượt quá 500 ký tự");
                etDescription.requestFocus();
                return;
            }

            // Nếu tất cả hợp lệ, tiến hành lưu vào Database
            new Thread(() -> {
                if (roomToEdit == null) {
                    Room newRoom = new Room();
                    newRoom.roomType = type;
                    newRoom.price = price;
                    newRoom.capacity = capacity;
                    newRoom.quantity = quantity;
                    newRoom.description = desc;
                    db.roomDao().insert(newRoom);
                } else {
                    roomToEdit.roomType = type;
                    roomToEdit.price = price;
                    roomToEdit.capacity = capacity;
                    roomToEdit.quantity = quantity;
                    roomToEdit.description = desc;
                    db.roomDao().update(roomToEdit);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng chỉ nhập số vào các trường tương ứng", Toast.LENGTH_SHORT).show();
        }
    }
}
