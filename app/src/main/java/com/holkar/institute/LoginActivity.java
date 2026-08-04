package com.holkar.institute;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogleLogin, btnFacebookLogin, btnPhoneLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        btnPhoneLogin = findViewById(R.id.btnPhoneLogin);

        // Real Firebase Email Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication Failed! Check email/password.", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // Restrict fake social logins until real API keys are integrated
        btnGoogleLogin.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Google Sign-In requires Firebase console configuration.", Toast.LENGTH_LONG).show());
        btnFacebookLogin.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Facebook login requires SDK configuration.", Toast.LENGTH_LONG).show());
        btnPhoneLogin.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Phone OTP requires Firebase Phone Auth setup.", Toast.LENGTH_LONG).show());
    }
}
