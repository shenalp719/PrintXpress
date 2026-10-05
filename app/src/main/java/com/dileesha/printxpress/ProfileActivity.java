package com.dileesha.printxpress;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    TextView tvEmail;
    EditText etName, etPhone, etAddress;
    Button btnSaveProfile, btnLogout, btnViewSavedDesigns;
    ImageButton btnEditEmail;
    ImageView ivProfilePic;
    DatabaseHelper db;
    String currentUserEmail;
    BottomNavigationView bottomNavigationView;

    private static final int PICK_IMAGE_REQUEST = 200;

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
        btnViewSavedDesigns = findViewById(R.id.btnViewSavedDesigns);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        currentUserEmail = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).getString("LOGGED_IN_EMAIL", "Unknown User");
        tvEmail.setText(currentUserEmail);

        loadProfileData();

        // 1. Pick Profile Picture from Gallery (Updated for Persistent Access)
        ivProfilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // 2. Secure Email Edit Logic
        btnEditEmail.setOnClickListener(v -> requestPasswordForEmailChange());

        // 3. Standard Profile Save Logic
        btnSaveProfile.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            boolean isUpdated = db.updateProfileDetails(currentUserEmail, name, phone, address);
            if (isUpdated) {
                Toast.makeText(ProfileActivity.this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Error saving profile", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. View Saved Designs Logic
        btnViewSavedDesigns.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SavedDesignsActivity.class)));

        // 5. Logout Logic
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 6. Bottom Navigation Logic
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Request persistent permission so it survives app restarts
                try {
                    getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException e) {
                    // Ignore if the provider doesn't support persistable permissions
                }

                db.updateProfilePhoto(currentUserEmail, imageUri.toString());
                ivProfilePic.setImageURI(imageUri);
                Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProfileData() {
        Cursor cursor = db.getUserDetails(currentUserEmail);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(3);
            String address = cursor.getString(4);
            String phone = cursor.getString(5);
            String photoUri = cursor.getString(6);

            if (name != null) etName.setText(name);
            if (address != null) etAddress.setText(address);
            if (phone != null) etPhone.setText(phone);

            if (photoUri != null && !photoUri.isEmpty()) {
                try {
                    Uri uri = Uri.parse(photoUri);
                    // Safely test if we still have access BEFORE applying the image
                    getContentResolver().openInputStream(uri).close();
                    ivProfilePic.setImageURI(uri);
                } catch (Exception e) {
                    // If permission was lost, clear the broken URI from the database so it stops crashing
                    db.updateProfilePhoto(currentUserEmail, "");
                }
            }
        }
        cursor.close();
    }

    private void requestPasswordForEmailChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Security Check");
        builder.setMessage("Enter current password to modify email:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String enteredPassword = input.getText().toString();
            if (db.checkUser(currentUserEmail, enteredPassword)) {
                showNewEmailDialog();
            } else {
                Toast.makeText(ProfileActivity.this, "Access Denied: Incorrect Password", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showNewEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Email");
        builder.setMessage("Enter your new email address:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String newEmail = input.getText().toString().trim();

            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(ProfileActivity.this, "Invalid Email Format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.updateSecureEmail(currentUserEmail, newEmail)) {
                getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).edit().putString("LOGGED_IN_EMAIL", newEmail).apply();
                currentUserEmail = newEmail;
                tvEmail.setText(newEmail);
                Toast.makeText(ProfileActivity.this, "Email Updated Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "System Error updating email", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}