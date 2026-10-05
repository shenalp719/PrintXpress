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
            String identifier = etRegEmail.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            String confirmPassword = etRegConfirmPassword.getText().toString().trim();

            if (identifier.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Dual Validation Logic
            boolean isEmail = identifier.contains("@");
            boolean isPhone = identifier.matches("\\d+"); // Checks if it contains ONLY numbers

            if (isEmail) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if (isPhone) {
                if (identifier.length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Enter a valid email or 10-digit phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check for duplicates
            if (db.checkUserExists(identifier)) {
                Toast.makeText(RegisterActivity.this, "Account already exists with this email/phone", Toast.LENGTH_LONG).show();
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

            boolean isInserted = db.insertUser(identifier, password);
            if (isInserted) {
                Toast.makeText(RegisterActivity.this, "ACCOUNT CREATED. You may now login.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Registration Failed due to system error.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBackToLogin.setOnClickListener(v -> finish());
    }
}