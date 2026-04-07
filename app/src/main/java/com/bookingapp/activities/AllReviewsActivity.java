package com.bookingapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bookingapp.R;
import com.bookingapp.adapter.ReviewAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Review;
import java.util.ArrayList;
import java.util.List;

public class AllReviewsActivity extends AppCompatActivity {

    private RecyclerView rvAllReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> allReviews = new ArrayList<>();
    private List<Review> filteredReviews = new ArrayList<>();
    private AppDatabase db;
    private int hotelId;
    private int currentFilter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);

        hotelId = getIntent().getIntExtra("hotelId", -1);
        db = AppDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbarReviews);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_revert);
            getSupportActionBar().setTitle("Reviews");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvAllReviews = findViewById(R.id.rvAllReviews);
        rvAllReviews.setLayoutManager(new LinearLayoutManager(this));

        loadData();
        setupFilters();
    }

    private void loadData() {
        allReviews = db.reviewDao().getReviewsByHotelId(hotelId);
        applyFilter(currentFilter);
    }

    private void setupFilters() {
        findViewById(R.id.btnFilterAll).setOnClickListener(v -> applyFilter(0));
        findViewById(R.id.btnFilterPositive).setOnClickListener(v -> applyFilter(-1));
        findViewById(R.id.btnFilterNegative).setOnClickListener(v -> applyFilter(-2));
        findViewById(R.id.btnFilter5).setOnClickListener(v -> applyFilter(5));
        findViewById(R.id.btnFilter4).setOnClickListener(v -> applyFilter(4));
        findViewById(R.id.btnFilter3).setOnClickListener(v -> applyFilter(3));
        findViewById(R.id.btnFilter2).setOnClickListener(v -> applyFilter(2));
        findViewById(R.id.btnFilter1).setOnClickListener(v -> applyFilter(1));
    }

    private void applyFilter(int type) {
        currentFilter = type;
        filteredReviews.clear();
        if (type == 0) {
            filteredReviews.addAll(allReviews);
        } else if (type == -1) {
            for (Review r : allReviews) if (r.rating >= 3) filteredReviews.add(r);
        } else if (type == -2) {
            for (Review r : allReviews) if (r.rating < 3) filteredReviews.add(r);
        } else {
            for (Review r : allReviews) if ((int)r.rating == type) filteredReviews.add(r);
        }

        reviewAdapter = new ReviewAdapter(this, filteredReviews);
        reviewAdapter.setOnReviewLongClickListener(this::showOptionsDialog);
        rvAllReviews.setAdapter(reviewAdapter);
    }

    private void showOptionsDialog(Review review) {
        String[] options = {"Sửa đánh giá", "Xóa đánh giá"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tùy chọn đánh giá");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showEditReviewDialog(review);
            } else {
                showDeleteConfirmDialog(review);
            }
        });
        builder.show();
    }

    private void showEditReviewDialog(Review review) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_review, null);
        builder.setView(view);

        RatingBar rbInput = view.findViewById(R.id.rbInputRating);
        EditText etComment = view.findViewById(R.id.etInputComment);
        Button btnSubmit = view.findViewById(R.id.btnSubmitReview);
        
        rbInput.setRating(review.rating);
        etComment.setText(review.comment);
        btnSubmit.setText("Cập nhật");

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            review.rating = rbInput.getRating();
            review.comment = etComment.getText().toString().trim();
            db.reviewDao().update(review);
            Toast.makeText(this, "Đã cập nhật đánh giá", Toast.LENGTH_SHORT).show();
            loadData();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showDeleteConfirmDialog(Review review) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa đánh giá này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    db.reviewDao().delete(review);
                    Toast.makeText(this, "Đã xóa đánh giá", Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
