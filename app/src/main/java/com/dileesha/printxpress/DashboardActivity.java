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

    Button btnStickers, btnFlyers, btnTshirts, btnPosters, btnMugs, btnBanners, btnGallery, btnSupport;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Map the 6 new product buttons
        btnStickers = findViewById(R.id.btnStickers);
        btnFlyers = findViewById(R.id.btnFlyers);
        btnTshirts = findViewById(R.id.btnTshirts);
        btnPosters = findViewById(R.id.btnPosters);
        btnMugs = findViewById(R.id.btnMugs);
        btnBanners = findViewById(R.id.btnBanners);

        btnGallery = findViewById(R.id.btnGallery);
        btnSupport = findViewById(R.id.btnSupport);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Safely pass the text of the button to the Order Activity (Fixes the crash)
        View.OnClickListener productClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String productName = clickedButton.getText().toString();

                Intent intent = new Intent(DashboardActivity.this, OrderActivity.class);
                intent.putExtra("PRODUCT_NAME", productName);
                startActivity(intent);
            }
        };

        // Attach the listener to all 6 buttons
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