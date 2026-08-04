package com.holkar.institute;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

        btnGetOtp.setOnClickListener(v -> {
            userPhone = etPhone.getText().toString().trim();

            if (userPhone.isEmpty() || userPhone.length() != 10) {
                Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            Random random = new Random();
            generatedOtp = String.format("%06d", random.nextInt(1000000));

            // Testing / Development mode ke liye OTP ko direct Toast mein bhi dikha dete hain taaki testing mein rukawat na aaye
            Toast.makeText(this, "OTP Generated: " + generatedOtp, Toast.чити).show();
            
            // UI Switch to OTP step
            layoutPhoneStep.setVisibility(View.GONE);
            layoutOtpStep.setVisibility(View.VISIBLE);
        });

        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = etOtp.getText().toString().trim();

            if (enteredOtp.equals(generatedOtp)) {
                Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                layoutOtpStep.setVisibility(View.GONE);

                if (dbHelper.checkUserExists(userPhone)) {
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    layoutProfileStep.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "Invalid OTP. Please check and try again.", Toast.LENGTH_SHORT).show();
            }
        });

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
