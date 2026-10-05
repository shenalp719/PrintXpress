package com.dileesha.printxpress;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText etRegEmail, etRegPassword, etRegConfirmPassword;
    Button btnExecuteRegister, btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        btnExecuteRegister = findViewById(R.id.btnExecuteRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnExecuteRegister.setOnClickListener(v -> {
            String email = etRegEmail.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            String confirmPassword = etRegConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = db.insertUser(email, password);
            if (isInserted) {
                Toast.makeText(RegisterActivity.this, "ACCOUNT_CREATED. You may now login.", Toast.LENGTH_LONG).show();
                finish(); // Closes the register page and returns to login
            } else {
                Toast.makeText(RegisterActivity.this, "Registration Failed. Email may already exist.", Toast.LENGTH_SHORT).show();
            }
        });

        // Return to login screen without registering
        btnBackToLogin.setOnClickListener(v -> finish());
    }
}