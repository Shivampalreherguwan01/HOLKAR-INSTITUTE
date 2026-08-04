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

    private static final String FAST2SMS_API_KEY = "yDlocbw8VJgMlmpEULzHr4kQTK95iC0t";

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

            sendRealSmsViaApi(userPhone, generatedOtp);
        });

        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = etOtp.getText().toString().trim();

            if (enteredOtp.equals(generatedOtp)) {
                Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show();
                layoutOtpStep.setVisibility(View.GONE);

                if (dbHelper.checkUserExists(userPhone)) {
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    layoutProfileStep.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "Invalid OTP. Try again.", Toast.LENGTH_SHORT).show();
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

    private void sendRealSmsViaApi(String phone, String otp) {
        Toast.makeText(this, "Sending real SMS to " + phone + "...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                URL url = new URL("https://www.fast2sms.com/dev/bulkV2");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("authorization", FAST2SMS_API_KEY);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String message = "Your Holkar Institute OTP is " + otp;
                String postData = "route=q&message=" + URLEncoder.encode(message, "UTF-8") + 
                                  "&language=english&flash=0&numbers=" + phone;

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                BufferedReader reader;
                if (responseCode == 200) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (responseCode == 200) {
                        Toast.makeText(SignupActivity.this, "Real SMS Sent Successfully!", Toast.LENGTH_LONG).show();
                        layoutPhoneStep.setVisibility(View.GONE);
                        layoutOtpStep.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(SignupActivity.this, "API Error Code: " + responseCode, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(SignupActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}
