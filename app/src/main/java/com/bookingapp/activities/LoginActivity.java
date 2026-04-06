package com.bookingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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

        // Fix cứng tài khoản Vendor để test nhanh
        if (email.equals("admin@gmail.com") && password.equals("admin123")) {
            saveUserAndNavigate(999, "Admin Vendor", "vendor");
            return;
        }

        UserDao userDao = db.userDao();
        User user = userDao.login(email, password);

        if (user != null) {
            saveUserAndNavigate(user.id, user.name, user.role);
        } else {
            Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserAndNavigate(int id, String name, String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putInt("userId", id);
        editor.putString("userName", name);
        editor.putString("userRole", role); 
        editor.apply();

        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

        Intent intent;
        if ("vendor".equalsIgnoreCase(role)) {
            intent = new Intent(this, VendorMainActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
}
