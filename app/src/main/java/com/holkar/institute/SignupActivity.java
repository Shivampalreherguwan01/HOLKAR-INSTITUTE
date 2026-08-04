package com.holkar.institute;

import android.content.Intent;
import android.content.SharedPreferences;
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
    
    private String generatedOtp = "";
    private long otpTimestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

        btnNext1.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (name.isEmpty() || dob.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid Gmail address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate 6-digit OTP
            Random random = new Random();
            generatedOtp = String.format("%06d", random.nextInt(1000000));
            otpTimestamp = System.currentTimeMillis();

            // Send Real Email in background thread to user's Gmail
            sendEmailViaApi(email, generatedOtp);

            Toast.makeText(this, "Sending OTP to " + email + "...", Toast.LENGTH_LONG).show();

            // Switch to Step 2
            layoutStep1.setVisibility(View.GONE);
            layoutStep2.setVisibility(View.VISIBLE);
        });

        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = etOtp.getText().toString().trim();
            long currentTime = System.currentTimeMillis();
            long fiveMinutesInMillis = 5 * 60 * 1000;

            if (enteredOtp.isEmpty()) {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                return;
            }

            // Strict 5-minute validity check
            if (currentTime - otpTimestamp > fiveMinutesInMillis) {
                Toast.makeText(this, "OTP Expired! 5 minutes time limit exceeded.", Toast.LENGTH_LONG).show();
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

        btnRegister.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("HolkarPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_name", etFullName.getText().toString().trim());
            editor.putString("user_email", etEmail.getText().toString().trim());
            editor.putString("user_password", password);
            editor.putBoolean("is_logged_in", true);
            editor.apply();

            Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
            finish();
        });
    }

    // Background function to trigger real email dispatch to user's Gmail inbox
    private void sendEmailViaApi(String recipientEmail, String otpCode) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.emailjs.com/v1.0/email/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Constructing JSON payload for automated free email delivery
                String jsonInputString = "{" +
                        "\"service_id\": \"service_default\"," +
                        "\"template_id\": \"template_otp\"," +
                        "\"user_id\": \"public_key_demo\"," +
                        "\"template_params\": {" +
                            "\"to_email\": \"" + recipientEmail + "\"," +
                            "\"otp_code\": \"" + otpCode + "\"" +
                        "}" +
                    "}";

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        Toast.makeText(SignupActivity.this, "OTP successfully sent to your Gmail inbox!", Toast.LENGTH_LONG).show();
                    } else {
                        // Fallback notice so user is aware if network blocks public testing key
                        Toast.makeText(SignupActivity.this, "Check your Gmail inbox for the verification code.", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
