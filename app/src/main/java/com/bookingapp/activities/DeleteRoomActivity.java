package com.bookingapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bookingapp.R;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Room;

import java.util.Locale;

public class DeleteRoomActivity extends AppCompatActivity {

    private TextView tvRoomInfo;
    private Button btnConfirmDelete, btnCancel;
    private ImageButton btnBack;
    private AppDatabase db;
    private Room roomToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_room);

        db = AppDatabase.getInstance(this);

        tvRoomInfo = findViewById(R.id.tvRoomInfo);
        btnConfirmDelete = findViewById(R.id.btnConfirmDelete);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);

        int roomId = getIntent().getIntExtra("ROOM_ID", -1);

        new Thread(() -> {
            roomToDelete = db.roomDao().getRoomById(roomId);
            runOnUiThread(() -> {
                if (roomToDelete != null) {
                    String info = String.format(Locale.getDefault(),
                            "Loại phòng: %s\nGiá: %.2f$\nSức chứa: %d người\nSố lượng: %d",
                            roomToDelete.roomType, roomToDelete.price, roomToDelete.capacity, roomToDelete.quantity);
                    tvRoomInfo.setText(info);
                } else {
                    Toast.makeText(this, "Không tìm thấy phòng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();

        btnConfirmDelete.setOnClickListener(v -> {
            if (roomToDelete != null) {
                new Thread(() -> {
                    db.roomDao().delete(roomToDelete);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Đã xóa phòng thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                }).start();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());
    }
}
