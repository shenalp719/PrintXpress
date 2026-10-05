package com.dileesha.printxpress;

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
    String currentIdentifier;
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

        currentIdentifier = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).getString("LOGGED_IN_EMAIL", "Unknown User");

        // Dynamic UI adjustment based on login method
        if (currentIdentifier.contains("@")) {
            tvEmail.setText("Email: " + currentIdentifier);
        } else {
            tvEmail.setText("Account ID: " + currentIdentifier);
            etPhone.setVisibility(View.GONE); // Hide redundant phone field
        }

        loadProfileData();

        ivProfilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnEditEmail.setOnClickListener(v -> requestPasswordForIdentifierChange());

        btnSaveProfile.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            boolean isUpdated = db.updateProfileDetails(currentIdentifier, name, phone, address);
            if (isUpdated) {
                Toast.makeText(ProfileActivity.this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Error saving profile", Toast.LENGTH_SHORT).show();
            }
        });

        btnViewSavedDesigns.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SavedDesignsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

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
                try {
                    getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException e) {
                    // Ignore
                }

                if (db.updateProfilePhoto(currentIdentifier, imageUri.toString())) {
                    ivProfilePic.setImageURI(imageUri);
                    Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error updating photo in database", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadProfileData() {
        Cursor cursor = db.getUserDetails(currentIdentifier);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(3);
            String address = cursor.getString(4);
            String phone = cursor.getString(5);
            String photoUri = cursor.getString(6);

            if (name != null) etName.setText(name);
            if (address != null) etAddress.setText(address);
            if (phone != null && currentIdentifier.contains("@")) etPhone.setText(phone);

            if (photoUri != null && !photoUri.isEmpty()) {
                try {
                    Uri uri = Uri.parse(photoUri);
                    getContentResolver().openInputStream(uri).close();
                    ivProfilePic.setImageURI(uri);
                } catch (Exception e) {
                    db.updateProfilePhoto(currentIdentifier, "");
                }
            }
        }
        cursor.close();
    }

    private void requestPasswordForIdentifierChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Security Check");
        builder.setMessage("Enter current password to modify Account ID:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String enteredPassword = input.getText().toString();
            if (db.checkUser(currentIdentifier, enteredPassword)) {
                showNewIdentifierDialog();
            } else {
                Toast.makeText(ProfileActivity.this, "Access Denied: Incorrect Password", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showNewIdentifierDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Account ID");
        builder.setMessage("Enter new email or 10-digit phone number:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String newId = input.getText().toString().trim();

            boolean isEmail = newId.contains("@");
            boolean isPhone = newId.matches("\\d+");

            if (isEmail && !android.util.Patterns.EMAIL_ADDRESS.matcher(newId).matches()) {
                Toast.makeText(ProfileActivity.this, "Invalid Email Format", Toast.LENGTH_SHORT).show();
                return;
            } else if (isPhone && newId.length() != 10) {
                Toast.makeText(ProfileActivity.this, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show();
                return;
            } else if (!isEmail && !isPhone) {
                Toast.makeText(ProfileActivity.this, "Enter a valid email or 10-digit phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.updateSecureIdentifier(currentIdentifier, newId)) {
                getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).edit().putString("LOGGED_IN_EMAIL", newId).apply();
                currentIdentifier = newId;

                if (currentIdentifier.contains("@")) {
                    tvEmail.setText("Email: " + currentIdentifier);
                    etPhone.setVisibility(View.VISIBLE);
                } else {
                    tvEmail.setText("Account ID: " + currentIdentifier);
                    etPhone.setVisibility(View.GONE);
                }

                Toast.makeText(ProfileActivity.this, "Account ID Updated Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "System Error updating Account ID", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}