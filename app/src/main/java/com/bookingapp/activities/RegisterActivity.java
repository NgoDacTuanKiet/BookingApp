package com.bookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.bookingapp.R;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.dal.dao.UserDao;
import com.bookingapp.model.User;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView tvLoginLink;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "BookingDB"
        ).fallbackToDestructiveMigration() // Thêm dòng này để fix crash do database version
        .allowMainThreadQueries().build();

        btnRegister.setOnClickListener(v -> register());

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void register() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Password (min 8 characters, letters, numbers, and special chars)
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        if (!Pattern.compile(passwordPattern).matcher(password).matches()) {
            Toast.makeText(this, "Mật khẩu phải từ 8 ký tự, bao gồm chữ, số và ký tự đặc biệt", Toast.LENGTH_LONG).show();
            return;
        }

        // Confirm Password check
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDao userDao = db.userDao();

        // Check if email already exists
        if (userDao.getUserByEmail(email) != null) {
            Toast.makeText(this, "Email này đã được đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.name = name;
        user.email = email;
        user.password = password;
        user.role = "customer"; // Mặc định là user, có thể sửa thành "vendor" nếu cần test role vendor

        userDao.insert(user);

        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
}
