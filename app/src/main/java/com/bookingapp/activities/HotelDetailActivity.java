package com.bookingapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bookingapp.adapter.ReviewAdapter;
import com.bookingapp.adapter.RoomAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Hotel;
import com.bookingapp.model.Review;
import com.bookingapp.model.Room;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HotelDetailActivity extends AppCompatActivity {

    private Hotel hotel;
    private RecyclerView rvRooms, rvReviews;
    private RoomAdapter roomAdapter;
    private ReviewAdapter reviewAdapter;
    private AppDatabase db;
    private Button btnBooking, btnAddReview;
    private TextView tvAvgRating;
    private RatingBar rbAvgRating;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        db = AppDatabase.getInstance(this);
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
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

        tvAvgRating = findViewById(R.id.tvAvgRating);
        rbAvgRating = findViewById(R.id.rbAvgRating);
        btnAddReview = findViewById(R.id.btnAddReview);

        btnBooking = findViewById(R.id.btnBooking);

        rvRooms = findViewById(R.id.rvRooms);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));

        rvReviews = findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setNestedScrollingEnabled(false);

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
            loadReviews();
        }

        btnBooking.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đặt phòng đang phát triển.", Toast.LENGTH_SHORT).show();
        });

        btnAddReview.setOnClickListener(v -> showAddReviewDialog());
    }

    private void loadRooms() {
        List<Room> rooms = db.roomDao().getRoomsByHotelId(hotel.id);
        roomAdapter = new RoomAdapter(this, rooms);
        rvRooms.setAdapter(roomAdapter);
    }

    private void loadReviews() {
        List<Review> reviews = db.reviewDao().getReviewsByHotelId(hotel.id);
        reviewAdapter = new ReviewAdapter(this, reviews);
        rvReviews.setAdapter(reviewAdapter);

        if (reviews.size() > 0) {
            float avg = db.reviewDao().getAverageRating(hotel.id);
            tvAvgRating.setText(String.format(Locale.getDefault(), "%.1f", avg));
            rbAvgRating.setRating(avg);
        } else {
            tvAvgRating.setText("0.0");
            rbAvgRating.setRating(0);
        }
    }

    private void showAddReviewDialog() {
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            Toast.makeText(this, "Vui lòng đăng nhập để đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_review, null);
        builder.setView(view);

        RatingBar rbInput = view.findViewById(R.id.rbInputRating);
        EditText etComment = view.findViewById(R.id.etInputComment);
        Button btnSubmit = view.findViewById(R.id.btnSubmitReview);

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            float rating = rbInput.getRating();
            String comment = etComment.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = sharedPreferences.getInt("userId", -1);
            String userName = sharedPreferences.getString("userName", "Anonymous");

            // Đổi format thành dd-MM-yyyy
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            Review review = new Review(userId, hotel.id, userName, rating, comment, date);
            db.reviewDao().insert(review);

            Toast.makeText(this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
            loadReviews();
            dialog.dismiss();
        });

        dialog.show();
    }
}
