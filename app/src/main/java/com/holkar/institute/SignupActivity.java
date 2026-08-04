package com.holkar.institute;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private EditText etFullName, etDob, etEmail, etOtp, etPassword;
    private Button btnNext1, btnVerifyOtp, btnRegister;
    
    private String generatedOtp = "";
    private long otpTimestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Layout Steps
        layoutStep1 = findViewById(R.id.layoutStep1); // Name, DOB, Email
        layoutStep2 = findViewById(R.id.layoutStep2); // OTP Verification
        layoutStep3 = findViewById(R.id.layoutStep3); // Password Setup

        // Fields
        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        etPassword = findViewById(R.id.etPassword);

        // Buttons
        btnNext1 = findViewById(R.id.btnNext1);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnRegister = findViewById(R.id.btnRegister);

        // Step 1: User enters Name, DOB, Email -> Send OTP
        btnNext1.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (name.isEmpty() || dob.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate 6-digit Real OTP
            Random random = new Random();
            generatedOtp = String.format("%06d", random.nextInt(1000000));
            otpTimestamp = System.currentTimeMillis();

            // Simulation of sending OTP to email (In production, connect to Email API here)
            Toast.makeText(this, "OTP sent to " + email + ": " + generatedOtp, Toast.LENGTH_LONG).show();

            // Switch to Step 2 (OTP Input)
            layoutStep1.setVisibility(View.GONE);
            layoutStep2.setVisibility(View.VISIBLE);
        });

        // Step 2: Verify OTP (Valid for 5 Minutes)
        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = etOtp.getText().toString().trim();
            long currentTime = System.currentTimeMillis();
            long fiveMinutesInMillis = 5 * 60 * 1000;

            if (enteredOtp.isEmpty()) {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check 5 minutes expiration
            if (currentTime - otpTimestamp > fiveMinutesInMillis) {
                Toast.makeText(this, "OTP Expired! Please restart signup.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (enteredOtp.equals(generatedOtp)) {
                Toast.makeText(this, "Email Verified Successfully!", Toast.LENGTH_SHORT).show();
                // Switch to Step 3 (Password Setup)
                layoutStep2.setVisibility(View.GONE);
                layoutStep3.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Invalid OTP! Please check code.", Toast.LENGTH_SHORT).show();
            }
        });

        // Step 3: Set Password & Direct Login to Dashboard
        btnRegister.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save user profile locally
            SharedPreferences prefs = getSharedPreferences("HolkarPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_name", etFullName.getText().toString().trim());
            editor.putString("user_email", etEmail.getText().toString().trim());
            editor.putString("user_password", password);
            editor.putBoolean("is_logged_in", true);
            editor.apply();

            Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();

            // Direct entry to Dashboard (No login page redirection)
            startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
            finish();
        });
    }
}
