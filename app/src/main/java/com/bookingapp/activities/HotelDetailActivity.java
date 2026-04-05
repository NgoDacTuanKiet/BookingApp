package com.bookingapp.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bookingapp.R;
import com.bookingapp.model.Hotel;
import com.bumptech.glide.Glide;

public class HotelDetailActivity extends AppCompatActivity {

    private Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

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
        TextView tvBedrooms = findViewById(R.id.tvBedrooms);
        TextView tvBathrooms = findViewById(R.id.tvBathrooms);
        TextView tvGuestrooms = findViewById(R.id.tvGuestrooms);
        TextView tvDescription = findViewById(R.id.detailDescription);
        ImageView ivHotel = findViewById(R.id.detailHotelImage);

        TextView facWifi = findViewById(R.id.facWifi);
        TextView facPool = findViewById(R.id.facPool);
        TextView facFood = findViewById(R.id.facFood);
        TextView facPark = findViewById(R.id.facPark);

        if (hotel != null) {
            tvName.setText(hotel.name);
            tvPrice.setText("$" + hotel.price + "/Person");
            tvLocation.setText(hotel.city + ", " + hotel.address);
            tvBedrooms.setText(hotel.bedrooms + " Bedrooms");
            tvBathrooms.setText(hotel.bathrooms + " Bathroom");
            tvGuestrooms.setText(hotel.guestrooms + " Guestroom");
            tvDescription.setText(hotel.description);

            Glide.with(this)
                    .load(hotel.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivHotel);

            facWifi.setAlpha(hotel.hasWifi ? 1.0f : 0.3f);
            facPool.setAlpha(hotel.hasPool ? 1.0f : 0.3f);
            facFood.setAlpha(hotel.hasFoodCourt ? 1.0f : 0.3f);
            facPark.setAlpha(hotel.hasPark ? 1.0f : 0.3f);
        }
    }
}
