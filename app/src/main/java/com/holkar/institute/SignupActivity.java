package com.holkar.institute;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

        // Step 1: Input details & trigger real SMS via Fast2SMS API to mobile number
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

            if (dbHelper.checkEmailExists(phone)) {
                Toast.makeText(this, "Mobile number already registered! Please login.", Toast.LENGTH_LONG).show();
                return;
            }

            // Generate 6-digit secure OTP
            Random random = new Random();
            generatedOtp = String.format("%06d", random.nextInt(1000000));
            otpTimestamp = System.currentTimeMillis();

            // Send real SMS to student's mobile number
            sendRealSmsToMobile(phone, generatedOtp);

            Toast.makeText(this, "Sending real SMS OTP to " + phone + "...", Toast.LENGTH_LONG).show();

            layoutStep1.setVisibility(View.GONE);
            layoutStep2.setVisibility(View.VISIBLE);
        });

        // Step 2: Verify SMS OTP
        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = etOtp.getText().toString().trim();
            long currentTime = System.currentTimeMillis();
            long fiveMinutesInMillis = 5 * 60 * 1000;

            if (enteredOtp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentTime - otpTimestamp > fiveMinutesInMillis) {
                Toast.makeText(this, "OTP Expired! 5 minutes limit exceeded.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (enteredOtp.equals(generatedOtp)) {
                Toast.makeText(this, "Mobile Number Verified via SMS!", Toast.LENGTH_SHORT).show();
                layoutStep2.setVisibility(View.GONE);
                layoutStep3.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Invalid OTP code!", Toast.LENGTH_SHORT).show();
            }
        });

        // Step 3: Password setup & save student profile
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
                Toast.makeText(this, "Student Account Created Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Registration failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Background thread to send real SMS via Fast2SMS Quick SMS API
    private void sendRealSmsToMobile(String mobileNumber, String otpCode) {
        new Thread(() -> {
            try {
                String apiKey = "YOUR_FAST2SMS_API_KEY"; // Fast2SMS free/paid api key
                String message = "Your Holkar Institute verification OTP is " + otpCode + ". Valid for 5 minutes.";
                String encodedMessage = URLEncoder.encode(message, "UTF-8");
                
                String urlString = "https://www.fast2sms.com/dev/bulkV2?authorization=" + apiKey + 
                                   "&route=q&message=" + encodedMessage + 
                                   "&language=english&flash=0&numbers=" + mobileNumber;

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("cache-control", "no-cache");

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        Toast.makeText(SignupActivity.this, "Real SMS successfully sent to " + mobileNumber, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignupActivity.this, "SMS Gateway response received.", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
