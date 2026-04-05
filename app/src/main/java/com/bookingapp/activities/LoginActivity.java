package com.bookingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bookingapp.MainActivity;
import com.bookingapp.R;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.dal.dao.UserDao;
import com.bookingapp.model.User;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView tvGoRegister;

    AppDatabase db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        // Xóa trạng thái đăng nhập cũ ngay khi app khởi chạy vào màn hình Login
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();

        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        db = AppDatabase.getInstance(this);

        btnLogin.setOnClickListener(v -> login());

        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (edtEmail != null) edtEmail.setText("");
        if (edtPassword != null) edtPassword.setText("");
    }

    private void login() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDao userDao = db.userDao();
        User user = userDao.login(email, password);

        if (user != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putInt("userId", user.id);
            editor.putString("userName", user.name);
            editor.putString("userRole", user.role); // Lưu lại role
            editor.apply();

            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

            // Phân quyền chuyển hướng dựa trên Role
            Intent intent;
            if ("vendor".equalsIgnoreCase(user.role)) {
                intent = new Intent(this, VendorMainActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}
