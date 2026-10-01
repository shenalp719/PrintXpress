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

    Button btnBusinessCards, btnFlyers, btnMugs, btnTshirts, btnGallery, btnSupport;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnBusinessCards = findViewById(R.id.btnBusinessCards);
        btnFlyers = findViewById(R.id.btnFlyers);
        btnMugs = findViewById(R.id.btnMugs);
        btnTshirts = findViewById(R.id.btnTshirts);
        btnGallery = findViewById(R.id.btnGallery);
        btnSupport = findViewById(R.id.btnSupport);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        View.OnClickListener productClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String rawName = clickedButton.getText().toString();
                String cleanName = rawName.substring(rawName.indexOf("(") + 1, rawName.indexOf(")"));

                Intent intent = new Intent(DashboardActivity.this, OrderActivity.class);
                intent.putExtra("PRODUCT_NAME", cleanName);
                startActivity(intent);
            }
        };

        btnBusinessCards.setOnClickListener(productClickListener);
        btnFlyers.setOnClickListener(productClickListener);
        btnMugs.setOnClickListener(productClickListener);
        btnTshirts.setOnClickListener(productClickListener);

        btnGallery.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, GalleryActivity.class)));
        btnSupport.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, SupportActivity.class)));

        // CORRECTED: Uses DashboardActivity.this
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