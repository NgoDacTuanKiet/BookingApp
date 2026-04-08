package com.bookingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.activities.HotelDetailActivity;
import com.bookingapp.activities.LoginActivity;
import com.bookingapp.activities.UserProfileActivity;
import com.bookingapp.adapter.HotelAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Hotel;
import com.bookingapp.model.User;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvTopRated, rvExplore;
    private HotelAdapter topRatedAdapter, exploreAdapter;
    private List<Hotel> topRatedList = new ArrayList<>();
    private List<Hotel> exploreList = new ArrayList<>();
    private AppDatabase db;
    private EditText edtSearch;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView profileImage;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.bookingapp.utils.CloudinaryHelper.init(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        profileImage = findViewById(R.id.profile_image);

        ImageView headerBg = findViewById(R.id.header_background);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b")
                .centerCrop()
                .into(headerBg);

        updateProfileImage();

        profileImage.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                logout();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        edtSearch = findViewById(R.id.edtSearch);
        rvTopRated = findViewById(R.id.rvTopRated);
        rvExplore = findViewById(R.id.rvExplore);

        rvTopRated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvExplore.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        topRatedAdapter = new HotelAdapter(topRatedList, hotel -> {
            Intent intent = new Intent(MainActivity.this, HotelDetailActivity.class);
            intent.putExtra("hotel", hotel);
            startActivity(intent);
        });

        exploreAdapter = new HotelAdapter(exploreList, hotel -> {
            Intent intent = new Intent(MainActivity.this, HotelDetailActivity.class);
            intent.putExtra("hotel", hotel);
            startActivity(intent);
        });

        rvTopRated.setAdapter(topRatedAdapter);
        rvExplore.setAdapter(exploreAdapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHotels(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileImage();
        loadData(); // Tải lại dữ liệu mỗi khi quay lại trang chủ để cập nhật khách sạn mới
    }

    private void updateProfileImage() {
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId != -1) {
            User user = db.userDao().getUserById(userId);
            if (user != null && user.avatarUrl != null && !user.avatarUrl.isEmpty()) {
                Glide.with(this)
                        .load(user.avatarUrl)
                        .circleCrop()
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .into(profileImage);
            } else {
                Glide.with(this)
                        .load("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde")
                        .circleCrop()
                        .into(profileImage);
            }
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void searchHotels(String query) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Hotel> filtered = db.hotelDao().searchHotels(query);
            new Handler(Looper.getMainLooper()).post(() -> {
                exploreList.clear();
                exploreList.addAll(filtered);
                exploreAdapter.notifyDataSetChanged();
            });
        });
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            final List<Hotel> hotels = db.hotelDao().getAll();
            
            new Handler(Looper.getMainLooper()).post(() -> {
                topRatedList.clear();
                topRatedList.addAll(hotels);
                topRatedAdapter.notifyDataSetChanged();

                exploreList.clear();
                exploreList.addAll(hotels);
                exploreAdapter.notifyDataSetChanged();
            });
        });
    }
}
