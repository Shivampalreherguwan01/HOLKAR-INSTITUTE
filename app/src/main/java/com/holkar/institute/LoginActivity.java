package com.holkar.institute;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogleLogin, btnFacebookLogin, btnPhoneLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        btnPhoneLogin = findViewById(R.id.btnPhoneLogin);

        // Real SQLite Database Login Check
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isValidUser = dbHelper.checkUser(email, password);
                if (isValidUser) {
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Email or Password! Please Sign Up first.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Social login safety prompts
        btnGoogleLogin.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Please use Email/Password or register first.", Toast.LENGTH_SHORT).show());
        btnFacebookLogin.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Please use Email/Password or register first.", Toast.LENGTH_SHORT).show());
        btnPhoneLogin.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Please use Email/Password or register first.", Toast.LENGTH_SHORT).show());
    }
}
