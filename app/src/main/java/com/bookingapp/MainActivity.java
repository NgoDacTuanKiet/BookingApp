package com.bookingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.activities.HotelDetailActivity;
import com.bookingapp.activities.LoginActivity;
import com.bookingapp.activities.UserProfileActivity;
import com.bookingapp.adapter.HotelAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.fragments.FilterBottomSheet;
import com.bookingapp.model.Hotel;
import com.bookingapp.model.User;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements FilterBottomSheet.OnFilterAppliedListener {

    private RecyclerView rvTopRated, rvExplore, rvSearchResult;
    private HotelAdapter topRatedAdapter, exploreAdapter, searchResultAdapter;
    private List<Hotel> topRatedList = new ArrayList<>();
    private List<Hotel> exploreList = new ArrayList<>();
    private List<Hotel> searchResultList = new ArrayList<>();
    
    private AppDatabase db;
    private EditText edtSearch;
    private ImageButton btnFilter, btnSearch;
    private DrawerLayout drawerLayout;
    private LinearLayout defaultContentContainer, searchResultContainer;
    private TextView tvSearchResultCount;

    private double currentMinPrice = 0;
    private double currentMaxPrice = 10000000;
    private float currentMinRating = 0;
    private boolean currentHasWifi = false;
    private boolean currentHasPool = false;
    private boolean currentHasFoodCourt = false;
    private boolean currentHasPark = false;
    private boolean isFiltering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.bookingapp.utils.CloudinaryHelper.init(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        
        // Ánh xạ View
        drawerLayout = findViewById(R.id.drawer_layout);
        defaultContentContainer = findViewById(R.id.default_content_container);
        searchResultContainer = findViewById(R.id.search_result_container);
        tvSearchResultCount = findViewById(R.id.tvSearchResultCount);
        edtSearch = findViewById(R.id.edtSearch);
        btnFilter = findViewById(R.id.btnFilter);
        btnSearch = findViewById(R.id.btnSearch);

        // RecyclerViews
        rvTopRated = findViewById(R.id.rvTopRated);
        rvExplore = findViewById(R.id.rvExplore);
        rvSearchResult = findViewById(R.id.rvSearchResult);

        setupRecyclerViews();

        // Xử lý Sự kiện
        btnFilter.setOnClickListener(v -> {
            FilterBottomSheet filterSheet = new FilterBottomSheet();
            filterSheet.setOnFilterAppliedListener(this);
            filterSheet.show(getSupportFragmentManager(), "FilterBottomSheet");
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadData();
    }

    private void setupRecyclerViews() {
        rvTopRated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvExplore.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Kết quả tìm kiếm hiển thị dạng Grid 2 cột cho đẹp
        rvSearchResult.setLayoutManager(new GridLayoutManager(this, 2));

        topRatedAdapter = new HotelAdapter(topRatedList, this::openDetail);
        exploreAdapter = new HotelAdapter(exploreList, this::openDetail);
        searchResultAdapter = new HotelAdapter(searchResultList, this::openDetail);

        rvTopRated.setAdapter(topRatedAdapter);
        rvExplore.setAdapter(exploreAdapter);
        rvSearchResult.setAdapter(searchResultAdapter);
    }

    private void openDetail(Hotel hotel) {
        Intent intent = new Intent(this, HotelDetailActivity.class);
        intent.putExtra("hotel", hotel);
        startActivity(intent);
    }

    @Override
    public void onFilterApplied(double minPrice, double maxPrice, float minRating, boolean hasWifi, boolean hasPool, boolean hasFoodCourt, boolean hasPark) {
        this.currentMinPrice = minPrice;
        this.currentMaxPrice = maxPrice;
        this.currentMinRating = minRating;
        this.currentHasWifi = hasWifi;
        this.currentHasPool = hasPool;
        this.currentHasFoodCourt = hasFoodCourt;
        this.currentHasPark = hasPark;
        this.isFiltering = true;
        performSearch();
    }

    @Override
    public void onFilterCleared() {
        // Reset các giá trị lọc về mặc định
        this.currentMinPrice = 0;
        this.currentMaxPrice = 10000000;
        this.currentMinRating = 0;
        this.currentHasWifi = false;
        this.currentHasPool = false;
        this.currentHasFoodCourt = false;
        this.currentHasPark = false;
        this.isFiltering = false;
        
        // Xóa text trong thanh search
        edtSearch.setText("");
        
        // Quay lại giao diện mặc định
        searchResultContainer.setVisibility(View.GONE);
        defaultContentContainer.setVisibility(View.VISIBLE);
    }

    private void performSearch() {
        String query = edtSearch.getText().toString().trim();
        
        // Nếu không có text và không có bộ lọc thì quay về màn hình chính
        if (query.isEmpty() && !isFiltering) {
            searchResultContainer.setVisibility(View.GONE);
            defaultContentContainer.setVisibility(View.VISIBLE);
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            int wifi = currentHasWifi ? 1 : 0;
            int pool = currentHasPool ? 1 : 0;
            int food = currentHasFoodCourt ? 1 : 0;
            int park = currentHasPark ? 1 : 0;

            List<Hotel> filtered = db.hotelDao().advancedSearch(
                    query, currentMinPrice, currentMaxPrice, currentMinRating, wifi, pool, food, park
            );

            new Handler(Looper.getMainLooper()).post(() -> {
                searchResultList.clear();
                searchResultList.addAll(filtered);
                searchResultAdapter.notifyDataSetChanged();
                
                tvSearchResultCount.setText("Tìm thấy " + filtered.size() + " khách sạn phù hợp");
                
                // Chuyển đổi giao diện sang kết quả tìm kiếm
                defaultContentContainer.setVisibility(View.GONE);
                searchResultContainer.setVisibility(View.VISIBLE);
            });
        });
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Hotel> topRated = db.hotelDao().getTopRated();
            List<Hotel> all = db.hotelDao().getAll();
            new Handler(Looper.getMainLooper()).post(() -> {
                topRatedList.clear();
                topRatedList.addAll(topRated);
                topRatedAdapter.notifyDataSetChanged();
                exploreList.clear();
                exploreList.addAll(all);
                exploreAdapter.notifyDataSetChanged();
            });
        });
    }
}
