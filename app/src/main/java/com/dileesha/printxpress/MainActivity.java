package com.dileesha.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force Dark Mode across the entire app
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
        );

        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Redirects to the dedicated Registration Activity
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Unified Dual-Login Logic
        btnLogin.setOnClickListener(v -> {
            String identifier = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (identifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate and fetch the primary account ID
            String resolvedSessionId = db.authenticateUser(identifier, password);

            if (resolvedSessionId != null) {
                // Save the consistent primary ID to memory, preventing split order histories
                getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).edit().putString("LOGGED_IN_EMAIL", resolvedSessionId).apply();

                NotificationHelper.sendLoginAlert(MainActivity.this);
                NotificationHelper.sendPromoAlert(MainActivity.this);

                startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize Notification Channel
        NotificationHelper.createNotificationChannel(this);

        // Request Permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Login Logic inside MainActivity.java
        btnLogin.setOnClickListener(v -> {
            String identifier = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (identifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.checkUser(identifier, password)) {
                // The identifier (Email OR Phone) acts as the primary key for the session
                getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).edit().putString("LOGGED_IN_EMAIL", identifier).apply();

                NotificationHelper.sendLoginAlert(MainActivity.this);
                NotificationHelper.sendPromoAlert(MainActivity.this);

                startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}