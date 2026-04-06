package com.bookingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bookingapp.R;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.User;
import com.bookingapp.utils.CloudinaryHelper;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ivAvatar;
    private EditText edtName, edtEmail, edtPhone;
    private Button btnSave, btnChangeAvatar;
    private AppDatabase db;
    private User currentUser;
    private SharedPreferences sharedPreferences;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = AppDatabase.getInstance(this);
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        if (userId != -1) {
            currentUser = db.userDao().getUserById(userId);
        }

        ivAvatar = findViewById(R.id.ivUserProfileAvatar);
        edtName = findViewById(R.id.edtProfileName);
        edtEmail = findViewById(R.id.edtProfileEmail);
        edtPhone = findViewById(R.id.edtProfilePhone);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);

        if (currentUser != null) {
            edtName.setText(currentUser.name);
            edtEmail.setText(currentUser.email);
            edtPhone.setText(currentUser.phoneNumber);
            
            Glide.with(this)
                    .load(currentUser.avatarUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .circleCrop()
                    .into(ivAvatar);
        }

        btnChangeAvatar.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ivAvatar.setImageURI(selectedImageUri);
        }
    }

    private void saveProfile() {
        if (currentUser == null) return;

        String phone = edtPhone.getText().toString().trim();
        currentUser.phoneNumber = phone;

        if (selectedImageUri != null) {
            // Upload to Cloudinary
            uploadAvatarAndSave(phone);
        } else {
            // Chỉ cập nhật phone
            db.userDao().update(currentUser);
            Toast.makeText(this, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void uploadAvatarAndSave(String phone) {
        String publicId = CloudinaryHelper.getPublicId("users", String.valueOf(currentUser.id), "avatar");

        MediaManager.get().upload(selectedImageUri)
                .option("public_id", publicId)
                .option("unsigned", true)
                .option("upload_preset", CloudinaryHelper.getUploadPreset())
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(UserProfileActivity.this, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        currentUser.avatarUrl = imageUrl;
                        db.userDao().update(currentUser);
                        
                        // Cập nhật SharedPreferences để MainActivity biết ảnh đã đổi
                        sharedPreferences.edit().putString("userAvatar", imageUrl).apply();
                        
                        Toast.makeText(UserProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(UserProfileActivity.this, "Lỗi upload: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }
}
