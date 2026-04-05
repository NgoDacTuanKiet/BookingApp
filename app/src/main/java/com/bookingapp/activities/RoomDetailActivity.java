package com.bookingapp.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bookingapp.adapter.RoomGalleryAdapter;
import com.bookingapp.model.Room;
import com.bumptech.glide.Glide;

public class RoomDetailActivity extends AppCompatActivity {

    private Room room;
    private ImageView imgMain;
    private RecyclerView rvGallery;
    private RoomGalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        room = (Room) getIntent().getSerializableExtra("room");

        Toolbar toolbar = findViewById(R.id.roomToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        imgMain = findViewById(R.id.imgRoomDetail);
        TextView tvType = findViewById(R.id.tvDetailRoomType);
        TextView tvPrice = findViewById(R.id.tvDetailRoomPrice);
        TextView tvCapacity = findViewById(R.id.tvDetailRoomCapacity);
        TextView tvQuantity = findViewById(R.id.tvDetailRoomQuantity);
        TextView tvDesc = findViewById(R.id.tvDetailRoomDesc);
        
        rvGallery = findViewById(R.id.rvRoomGallery);
        rvGallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (room != null) {
            tvType.setText(room.roomType);
            tvPrice.setText("$" + room.price + " / đêm");
            tvCapacity.setText("Sức chứa: " + room.capacity + " người lớn");
            tvQuantity.setText("Số lượng phòng còn trống: " + room.quantity);
            tvDesc.setText(room.description);

            // Hiển thị ảnh mặc định ban đầu
            Glide.with(this)
                    .load(room.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgMain);

            // Thiết lập Gallery nếu có danh sách ảnh
            if (room.images != null && !room.images.isEmpty()) {
                galleryAdapter = new RoomGalleryAdapter(this, room.images, imageUrl -> {
                    // Khi nhấn vào ảnh nhỏ, đổi ảnh chính
                    Glide.with(RoomDetailActivity.this)
                            .load(imageUrl)
                            .centerCrop()
                            .into(imgMain);
                });
                rvGallery.setAdapter(galleryAdapter);
            }
        }
    }
}
