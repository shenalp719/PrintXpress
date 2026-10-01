package com.dileesha.printxpress;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView tvEmail;
    EditText etName, etAddress;
    Button btnSaveProfile, btnLogout;
    DatabaseHelper db;
    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = new DatabaseHelper(this);
        tvEmail = findViewById(R.id.tvEmail);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);

        android.content.SharedPreferences sharedPreferences = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("LOGGED_IN_EMAIL", "Unknown User");

        tvEmail.setText("Email: " + currentUserEmail);
        loadProfileData();

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String address = etAddress.getText().toString().trim();

                boolean isUpdated = db.updateProfile(currentUserEmail, name, address);
                if (isUpdated) {
                    Toast.makeText(ProfileActivity.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error saving profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the saved email from memory
                android.content.SharedPreferences sharedPreferences = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();

                // Clear the backstack and return to Login
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadProfileData() {
        Cursor cursor = db.getUserDetails(currentUserEmail);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(3); // COL_NAME index
            String address = cursor.getString(4); // COL_ADDRESS index

            if (name != null) etName.setText(name);
            if (address != null) etAddress.setText(address);
        }
        cursor.close();
    }
}