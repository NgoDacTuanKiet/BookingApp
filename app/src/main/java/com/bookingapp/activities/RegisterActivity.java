package com.bookingapp.activities;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.bookingapp.R;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.dal.dao.UserDao;
import com.bookingapp.model.User;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPassword;
    Button btnRegister;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "BookingDB"
        ).allowMainThreadQueries().build();

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDao userDao = db.userDao();

        if (userDao.getUserByEmail(email) != null) {
            Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.name = name;
        user.email = email;
        user.password = password;
        user.role = "customer";

        userDao.insert(user);

        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
}