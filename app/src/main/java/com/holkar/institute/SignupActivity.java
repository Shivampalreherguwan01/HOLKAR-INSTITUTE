package com.holkar.institute;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    private LinearLayout layoutStep1, layoutStep2, layoutStep3;
    private EditText etFullName, etDob, etPhone, etOtp, etPassword;
    private Button btnNext1, btnVerifyOtp, btnRegister;
    private DatabaseHelper dbHelper;

    private String generatedOtp = "";
    private long otpTimestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DatabaseHelper(this);

        layoutStep1 = findViewById(R.id.layoutStep1);
        layoutStep2 = findViewById(R.id.layoutStep2);
        layoutStep3 = findViewById(R.id.layoutStep3);

        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etPhone = findViewById(R.id.etPhone);
        etOtp = findViewById(R.id.etOtp);
        etPassword = findViewById(R.id.etPassword);

        btnNext1 = findViewById(R.id.btnNext1);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnRegister = findViewById(R.id.btnRegister);

        // Step 1: Input Name, DOB & Mobile Number
        btnNext1.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || dob.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.length() < 10) {
                Toast.makeText(this, "Enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate 6-digit SMS OTP
            Random random = new Random();
            generatedOtp = String.format("%06d", random.nextInt(1000000));
            otpTimestamp = System.currentTimeMillis();

            // Display OTP on screen for testing/verification purpose securely
            Toast.makeText(this, "SMS OTP sent to " + phone + ": " + generatedOtp, Toast.LENGTH_LONG).show();

            layoutStep1.setVisibility(View.GONE);
            layoutStep2.setVisibility(View.VISIBLE);
        });

        // Step 2: Verify Mobile OTP
        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = etOtp.getText().toString().trim();
            long currentTime = System.currentTimeMillis();
            long fiveMinutesInMillis = 5 * 60 * 1000;

            if (enteredOtp.isEmpty()) {
                Toast.makeText(this, "Please enter the SMS OTP code", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentTime - otpTimestamp > fiveMinutesInMillis) {
                Toast.makeText(this, "OTP Expired! 5 minutes limit exceeded.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (enteredOtp.equals(generatedOtp)) {
                Toast.makeText(this, "Mobile Number Verified Successfully!", Toast.LENGTH_SHORT).show();
                layoutStep2.setVisibility(View.GONE);
                layoutStep3.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Invalid OTP code!", Toast.LENGTH_SHORT).show();
            }
        });

        // Step 3: Password setup & save account
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
                Toast.makeText(this, "Registration failed! Number might already exist.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
