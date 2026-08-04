package com.holkar.institute;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Direct route to Signup/OTP activity for unified PW/KGS style flow
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }
}
