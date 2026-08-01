package com.holkar.institute;

import android.content.SharedPreferences;
import android.os.Bundle;
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

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // SharedPreferences mein user ka data save kar rahe hain (100% Free & Local)
                    SharedPreferences prefs = getSharedPreferences("HolkarPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("saved_email", email);
                    editor.putString("saved_password", password);
                    editor.apply();

                    Toast.makeText(SignupActivity.this, "Signup Successful! Now you can Login.", Toast.LENGTH_LONG).show();
                    finish(); // Back to login
                }
            }
        });
    }
}
