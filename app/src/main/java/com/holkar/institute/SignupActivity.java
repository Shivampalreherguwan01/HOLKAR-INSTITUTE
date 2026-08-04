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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private LinearLayout layoutStep1, layoutStep2;
    private EditText etFullName, etDob, etEmail, etPassword;
    private Button btnRegister, btnCheckVerification;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        layoutStep1 = findViewById(R.id.layoutStep1);
        layoutStep2 = findViewById(R.id.layoutStep2);

        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnCheckVerification = findViewById(R.id.btnCheckVerification);

        // Step 1: Create account and send official Google verification email to Gmail inbox
        btnRegister.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || dob.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid Gmail address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user in Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Send real verification link/email to user's Gmail inbox
                                user.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                    if (emailTask.isSuccessful()) {
                                        Toast.makeText(this, "Verification email sent to your Gmail inbox! Please check.", Toast.LENGTH_LONG).show();
                                        layoutStep1.setVisibility(View.GONE);
                                        layoutStep2.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(this, "Failed to send email: " + emailTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Step 2: Check if user clicked the link in their Gmail inbox
        btnCheckVerification.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (user.isEmailVerified()) {
                        Toast.makeText(this, "Email Verified Successfully! Opening App...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Email not verified yet! Please check your Gmail link.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
