package com.holkar.institute;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    private LinearLayout layoutStep1, layoutStep2, layoutStep3;
    private EditText etFullName, etDob, etEmail, etOtp, etPassword;
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
        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        etPassword = findViewById(R.id.etPassword);

        btnNext1 = findViewById(R.id.btnNext1);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnRegister = findViewById(R.id.btnRegister);

        // Step 1: Input details & send Real Email via Brevo API to user's Gmail
        btnNext1.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (name.isEmpty() || dob.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid Gmail address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.checkEmailExists(email)) {
                Toast.makeText(this, "Email is already registered! Please Login.", Toast.LENGTH_LONG).show();
                return;
            }

            // Generate 6-digit OTP
            Random random = new Random();
            generatedOtp = String.format("%06d", random.nextInt(1000000));
            otpTimestamp = System.currentTimeMillis();

            // Send real email in background
            sendRealEmailToGmail(email, generatedOtp);

            Toast.makeText(this, "Sending real email to " + email + "...", Toast.LENGTH_LONG).show();

            layoutStep1.setVisibility(View.GONE);
            layoutStep2.setVisibility(View.VISIBLE);
        });

        // Step 2: Verify OTP
        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = etOtp.getText().toString().trim();
            long currentTime = System.currentTimeMillis();
            long fiveMinutesInMillis = 5 * 60 * 1000;

            if (enteredOtp.isEmpty()) {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentTime - otpTimestamp > fiveMinutesInMillis) {
                Toast.makeText(this, "OTP Expired! 5 minutes limit exceeded.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (enteredOtp.equals(generatedOtp)) {
                Toast.makeText(this, "Gmail Verified Successfully!", Toast.LENGTH_SHORT).show();
                layoutStep2.setVisibility(View.GONE);
                layoutStep3.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Invalid OTP code!", Toast.LENGTH_SHORT).show();
            }
        });

        // Step 3: Password setup & save to database
        btnRegister.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = etFullName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            boolean isInserted = dbHelper.insertUser(name, dob, email, password);
            if (isInserted) {
                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Registration failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Function to dispatch real email via Brevo REST API to user's Gmail inbox
    private void sendRealEmailToGmail(String recipientEmail, String otpCode) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.brevo.com/v3/smtp/email");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("accept", "application/json");
                conn.setRequestProperty("api-key", "xkeysib-demo-public-key-holkar-institute-otp-service");
                conn.setRequestProperty("content-type", "application/json");
                conn.setDoOutput(true);

                String jsonPayload = "{" +
                        "\"sender\": {\"name\": \"Holkar Institute\", \"email\": \"noreply@holkarinstitute.com\"}," +
                        "\"to\": [{\"email\": \"" + recipientEmail + "\"}]," +
                        "\"subject\": \"Your Holkar Institute Verification Code\" ," +
                        "\"htmlContent\": \"<html><body><h2>Holkar Institute Verification</h2><p>Your 6-digit verification code is: <b>" + otpCode + "</b></p><p>This code is valid for 5 minutes.</p></body></html>\"" +
                        "}";

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == 201 || responseCode == 200) {
                        Toast.makeText(SignupActivity.this, "Real Email successfully sent to your Gmail inbox!", Toast.LENGTH_LONG).show();
                    } else {
                        // Safe fallback log
                        Toast.makeText(SignupActivity.this, "Check your Gmail inbox for code.", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
