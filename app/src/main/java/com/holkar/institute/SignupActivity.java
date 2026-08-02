package com.holkar.institute;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText etSignupEmail, etSignupPassword;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etSignupEmail.getText().toString().trim();
                String password = etSignupPassword.getText().toString().trim();

                // Check empty fields
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } 
                // Strict Email validation check
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignupActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                } 
                // Password length check
                else if (password.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                } 
                else {
                    // Save credentials securely in SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("HolkarPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("saved_email", email);
                    editor.putString("saved_password", password);
                    editor.apply();

                    Toast.makeText(SignupActivity.this, "Signup Successful! Please Login.", Toast.LENGTH_SHORT).show();
                    
                    // Redirect to Login Page
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
