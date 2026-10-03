package com.dileesha.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    // ADDED btnBusinessCards here
    Button btnBusinessCards, btnStickers, btnFlyers, btnTshirts, btnPosters, btnMugs, btnBanners, btnGallery, btnSupport;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Map all 7 product buttons
        btnBusinessCards = findViewById(R.id.btnBusinessCards); // ADDED this line
        btnStickers = findViewById(R.id.btnStickers);
        btnFlyers = findViewById(R.id.btnFlyers);
        btnTshirts = findViewById(R.id.btnTshirts);
        btnPosters = findViewById(R.id.btnPosters);
        btnMugs = findViewById(R.id.btnMugs);
        btnBanners = findViewById(R.id.btnBanners);

        btnGallery = findViewById(R.id.btnGallery);
        btnSupport = findViewById(R.id.btnSupport);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

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

        // Attach the listener to all 7 buttons
        btnBusinessCards.setOnClickListener(productClickListener); // ADDED this line
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