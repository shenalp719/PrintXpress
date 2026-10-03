package com.dileesha.printxpress;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    TextView tvEmail;
    EditText etName, etPhone, etAddress;
    Button btnSaveProfile, btnLogout;
    ImageButton btnEditEmail;
    DatabaseHelper db;
    String currentUserEmail;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = new DatabaseHelper(this);
        tvEmail = findViewById(R.id.tvEmail);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditEmail = findViewById(R.id.btnEditEmail);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        android.content.SharedPreferences sharedPreferences = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("LOGGED_IN_EMAIL", "Unknown User");

        tvEmail.setText(currentUserEmail);
        loadProfileData();

        // 1. Secure Email Edit Logic
        btnEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPasswordForEmailChange();
            }
        });

        // 2. Standard Profile Save Logic
        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String address = etAddress.getText().toString().trim();

                boolean isUpdated = db.updateProfileDetails(currentUserEmail, name, phone, address);
                if (isUpdated) {
                    Toast.makeText(ProfileActivity.this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error saving profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 3. Logout Logic
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.SharedPreferences sharedPreferences = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();

                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // 4. Bottom Navigation Logic
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_orders) {
                startActivity(new Intent(ProfileActivity.this, MyOrdersActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void loadProfileData() {
        Cursor cursor = db.getUserDetails(currentUserEmail);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(3);
            String address = cursor.getString(4);
            String phone = cursor.getString(5); // New Phone Column

            if (name != null) etName.setText(name);
            if (address != null) etAddress.setText(address);
            if (phone != null) etPhone.setText(phone);
        }
        cursor.close();
    }

    private void requestPasswordForEmailChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SECURITY_AUTH_REQUIRED");
        builder.setMessage("Enter current password to modify email:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredPassword = input.getText().toString();
                if (db.checkUser(currentUserEmail, enteredPassword)) {
                    // Password correct, show new email dialog
                    showNewEmailDialog();
                } else {
                    Toast.makeText(ProfileActivity.this, "ACCESS DENIED: Incorrect Password", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showNewEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("UPDATE_EMAIL");
        builder.setMessage("Enter your new email address:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newEmail = input.getText().toString().trim();

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    Toast.makeText(ProfileActivity.this, "Invalid Email Format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.updateSecureEmail(currentUserEmail, newEmail)) {
                    // Update SharedPreferences with the new email
                    android.content.SharedPreferences sharedPreferences = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE);
                    sharedPreferences.edit().putString("LOGGED_IN_EMAIL", newEmail).apply();

                    currentUserEmail = newEmail;
                    tvEmail.setText(newEmail);
                    Toast.makeText(ProfileActivity.this, "EMAIL_UPDATED_SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "System Error updating email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}