package com.bookingapp.activities;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "BookingDB"
        ).allowMainThreadQueries().build();

        btnLogin.setOnClickListener(v -> login());

        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        edtEmail.setText("");
        edtPassword.setText("");
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
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}