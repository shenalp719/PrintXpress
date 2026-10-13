package com.dileesha.printxpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {


    Button btnBusinessCards, btnStickers, btnFlyers, btnTshirts, btnPosters, btnMugs, btnBanners, btnGallery, btnSupport;
    BottomNavigationView bottomNavigationView;
    TextView tvGreeting; // 1. Declared the new TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Map all 7 product buttons
        btnBusinessCards = findViewById(R.id.btnBusinessCards);
        btnStickers = findViewById(R.id.btnStickers);
        btnFlyers = findViewById(R.id.btnFlyers);
        btnTshirts = findViewById(R.id.btnTshirts);
        btnPosters = findViewById(R.id.btnPosters);
        btnMugs = findViewById(R.id.btnMugs);
        btnBanners = findViewById(R.id.btnBanners);

        btnGallery = findViewById(R.id.btnGallery);
        btnSupport = findViewById(R.id.btnSupport);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 2. Map the greeting TextView
        tvGreeting = findViewById(R.id.tvGreeting);

        // 3. Retrieve the email and set the dynamic greeting
        SharedPreferences prefs = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE);
        String currentUserEmail = prefs.getString("LOGGED_IN_EMAIL", "Unknown");

        String userName = "User";
        if (!currentUserEmail.equals("Unknown") && currentUserEmail.contains("@")) {
            userName = currentUserEmail.split("@")[0]; // Get everything before '@'
            if (userName.length() > 0) {
                // Capitalize the first letter
                userName = userName.substring(0, 1).toUpperCase() + userName.substring(1);
            }
        }
        tvGreeting.setText("Hello " + userName + "!");


        // Bulletproof Click Listener
        View.OnClickListener productClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = "";
                int id = v.getId();

                if (id == R.id.btnBusinessCards) productName = "Business Cards";
                else if (id == R.id.btnStickers) productName = "Stickers";
                else if (id == R.id.btnFlyers) productName = "Flyers";
                else if (id == R.id.btnPosters) productName = "Posters";
                else if (id == R.id.btnMugs) productName = "Custom Mugs";
                else if (id == R.id.btnTshirts) productName = "Custom T-Shirts";
                else if (id == R.id.btnBanners) productName = "Banners";

                // Routes to ProductPresetActivity
                Intent intent = new Intent(DashboardActivity.this, ProductPresetActivity.class);
                intent.putExtra("PRODUCT_NAME", productName);
                startActivity(intent);
            }
        };

        // Listeners
        btnBusinessCards.setOnClickListener(productClickListener);
        btnStickers.setOnClickListener(productClickListener);
        btnFlyers.setOnClickListener(productClickListener);
        btnTshirts.setOnClickListener(productClickListener);
        btnPosters.setOnClickListener(productClickListener);
        btnMugs.setOnClickListener(productClickListener);
        btnBanners.setOnClickListener(productClickListener);

        btnGallery.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, GalleryActivity.class)));
        btnSupport.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, SupportActivity.class)));

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_orders) {
                    startActivity(new Intent(DashboardActivity.this, MyOrdersActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            }
        });

    }
}