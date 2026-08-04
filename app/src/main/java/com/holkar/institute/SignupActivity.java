package com.holkar.institute;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private LinearLayout layoutStep1, layoutStep2;
    private EditText etFullName, etDob, etPhone, etPassword;
    private Button btnNext1, btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DatabaseHelper(this);

        layoutStep1 = findViewById(R.id.layoutStep1);
        layoutStep2 = findViewById(R.id.layoutStep2);

        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);

        btnNext1 = findViewById(R.id.btnNext1);
        btnRegister = findViewById(R.id.btnRegister);

        // Step 1: Details validation & move to password setup
        btnNext1.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || dob.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.length() != 10) {
                Toast.makeText(this, "Enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            layoutStep1.setVisibility(View.GONE);
            layoutStep2.setVisibility(View.VISIBLE);
        });

        // Step 2: Password setup & account creation
        btnRegister.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = etFullName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            boolean isInserted = dbHelper.insertUser(name, dob, phone, password);
            if (isInserted) {
                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Registration failed! Number already exists.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
