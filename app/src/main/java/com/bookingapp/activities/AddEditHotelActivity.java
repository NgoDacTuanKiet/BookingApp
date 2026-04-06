package com.bookingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bookingapp.R;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Hotel;
import com.bookingapp.utils.CloudinaryHelper;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;
import java.util.UUID;

public class AddEditHotelActivity extends AppCompatActivity {

    private EditText etName, etAddress, etCity, etDescription, etPrice;
    private CheckBox cbWifi, cbPool, cbFood, cbPark;
    private ImageView ivHotelPreview;
    private Button btnSelectImage, btnSave;
    private TextView tvFormTitle;

    private AppDatabase db;
    private Hotel currentHotel;
    private int vendorId;
    private Uri selectedImageUri;
    private String uploadedImageUrl;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivHotelPreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_hotel);

        db = AppDatabase.getInstance(this);
        CloudinaryHelper.init(this);
        
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        vendorId = sharedPreferences.getInt("userId", -1);

        initViews();

        if (getIntent().hasExtra("hotel")) {
            currentHotel = (Hotel) getIntent().getSerializableExtra("hotel");
            fillData(currentHotel);
            tvFormTitle.setText("Chỉnh sửa khách sạn");
            uploadedImageUrl = currentHotel.imageUrl;
        }

        btnSelectImage.setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImageAndSave();
            } else {
                saveHotel();
            }
        });
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        cbWifi = findViewById(R.id.cbWifi);
        cbPool = findViewById(R.id.cbPool);
        cbFood = findViewById(R.id.cbFood);
        cbPark = findViewById(R.id.cbPark);
        ivHotelPreview = findViewById(R.id.ivHotelPreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);
        tvFormTitle = findViewById(R.id.tvFormTitle);
    }

    private void fillData(Hotel hotel) {
        etName.setText(hotel.name);
        etAddress.setText(hotel.address);
        etCity.setText(hotel.city);
        etDescription.setText(hotel.description);
        etPrice.setText(String.valueOf(hotel.price));
        cbWifi.setChecked(hotel.hasWifi);
        cbPool.setChecked(hotel.hasPool);
        cbFood.setChecked(hotel.hasFoodCourt);
        cbPark.setChecked(hotel.hasPark);
        Glide.with(this).load(hotel.imageUrl).into(ivHotelPreview);
    }

    private void uploadImageAndSave() {
        // SỬA Ở ĐÂY: Dùng CloudinaryHelper.getUploadPreset() của nhóm bạn
        MediaManager.get().upload(selectedImageUri)
                .unsigned(CloudinaryHelper.getUploadPreset())
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        btnSave.setEnabled(false);
                        btnSave.setText("Đang tải ảnh...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        uploadedImageUrl = (String) resultData.get("secure_url");
                        saveHotel();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(AddEditHotelActivity.this, "Lỗi tải ảnh: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                        btnSave.setText("Lưu khách sạn");
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    private void saveHotel() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || city.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(true);
            btnSave.setText("Lưu khách sạn");
            return;
        }

        double price = Double.parseDouble(priceStr);

        if (currentHotel == null) {
            Hotel newHotel = new Hotel(vendorId, name, address, city, description, 5.0, uploadedImageUrl, price, 1, 1, 1, 
                cbWifi.isChecked(), cbPool.isChecked(), cbFood.isChecked(), cbPark.isChecked());
            db.hotelDao().insert(newHotel);
            Toast.makeText(this, "Thêm khách sạn thành công", Toast.LENGTH_SHORT).show();
        } else {
            currentHotel.name = name;
            currentHotel.address = address;
            currentHotel.city = city;
            currentHotel.description = description;
            currentHotel.price = price;
            currentHotel.imageUrl = uploadedImageUrl;
            currentHotel.hasWifi = cbWifi.isChecked();
            currentHotel.hasPool = cbPool.isChecked();
            currentHotel.hasFoodCourt = cbFood.isChecked();
            currentHotel.hasPark = cbPark.isChecked();
            db.hotelDao().update(currentHotel);
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
