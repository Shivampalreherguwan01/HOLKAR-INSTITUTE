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

                boolean isEmailRegistered = dbHelper.checkEmailExists(email);
                if (!isEmailRegistered) {
                    Toast.makeText(LoginActivity.this, "Error: This email is not registered. Please Sign Up first!", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean isValidUser = dbHelper.checkUserCredentials(email, password);
                if (isValidUser) {
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect Password! Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        View.OnClickListener disabledSocialClick = v -> 
            Toast.makeText(LoginActivity.this, "Social logins are disabled. Please use Email and Password.", Toast.LENGTH_SHORT).show();

        btnGoogleLogin.setOnClickListener(disabledSocialClick);
        btnFacebookLogin.setOnClickListener(disabledSocialClick);
        btnPhoneLogin.setOnClickListener(disabledSocialClick);
    }
}
