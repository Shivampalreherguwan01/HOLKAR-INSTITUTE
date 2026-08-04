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

    private LinearLayout layoutPhoneStep, layoutOtpStep, layoutProfileStep;
    private EditText etPhone, etOtp, etFullName, etDob;
    private Button btnGetOtp, btnVerifyOtp, btnCompleteRegister;
    private DatabaseHelper dbHelper;

    private String generatedOtp = "";
    private String userPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DatabaseHelper(this);

        layoutPhoneStep = findViewById(R.id.layoutPhoneStep);
        layoutOtpStep = findViewById(R.id.layoutOtpStep);
        layoutProfileStep = findViewById(R.id.layoutProfileStep);

        etPhone = findViewById(R.id.etPhone);
        etOtp = findViewById(R.id.etOtp);
        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);

        btnGetOtp = findViewById(R.id.btnGetOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnCompleteRegister = findViewById(R.id.btnCompleteRegister);

        // Step 1: Enter Mobile Number & Generate OTP (PW Style)
        btnGetOtp.setOnClickListener(v -> {
            userPhone = etPhone.getText().toString().trim();

            if (userPhone.isEmpty() || userPhone.length() != 10) {
                Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate secure 4-digit OTP
            Random random = new Random();
            generatedOtp = String.format("%04d", random.nextInt(10000));

            // Simulation of OTP delivery notice (Safe & Crash-free)
            Toast.makeText(this, "OTP sent successfully to " + userPhone + " [Code: " + generatedOtp + "]", Toast.LENGTH_LONG).show();

            layoutPhoneStep.setVisibility(View.GONE);
            layoutOtpStep.setVisibility(View.VISIBLE);
        });

        // Step 2: Verify OTP
        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = etOtp.getText().toString().trim();

            if (enteredOtp.equals(generatedOtp)) {
                Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show();
                layoutOtpStep.setVisibility(View.GONE);

                // Check if user already exists in database
                if (dbHelper.checkUserExists(userPhone)) {
                    // Existing student -> Direct login to Dashboard
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    // New student -> Ask profile details (Name & DOB) like PW/KGS signup
                    layoutProfileStep.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "Invalid OTP. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Step 3: Complete Profile for New Students
        btnCompleteRegister.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();

            if (name.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Please enter your name and date of birth", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean saved = dbHelper.registerOrUpdateUser(userPhone, name, dob);
            if (saved) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to save profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
