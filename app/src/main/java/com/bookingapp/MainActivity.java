package com.bookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.activities.HotelDetailActivity;
import com.bookingapp.adapter.HotelAdapter;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Hotel;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        // Fixed background image for Header
        ImageView headerBg = findViewById(R.id.header_background);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b")
                .centerCrop()
                .into(headerBg);

        // Set real profile image instead of default robot icon
        ImageView profileImage = findViewById(R.id.profile_image);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde")
                .circleCrop()
                .into(profileImage);

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
            List<Hotel> hotels = db.hotelDao().getAll();
            if (hotels.isEmpty()) {
                String json = loadJSONFromAsset();
                if (json != null) {
                    List<Hotel> hotelsFromJson = new Gson().fromJson(json, new TypeToken<List<Hotel>>(){}.getType());
                    db.hotelDao().insertAll(hotelsFromJson.toArray(new Hotel[0]));
                    hotels = db.hotelDao().getAll();
                }
            }

            final List<Hotel> finalHotels = hotels;
            new Handler(Looper.getMainLooper()).post(() -> {
                topRatedList.clear();
                topRatedList.addAll(finalHotels);
                topRatedAdapter.notifyDataSetChanged();

                exploreList.clear();
                exploreList.addAll(finalHotels);
                exploreAdapter.notifyDataSetChanged();
            });
        });
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("hotels.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
