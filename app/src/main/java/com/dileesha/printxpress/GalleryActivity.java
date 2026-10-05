package com.dileesha.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GalleryActivity extends AppCompatActivity {

    LinearLayout cardGallery1, cardGallery2, cardGallery3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Link the full cards instead of just the images
        cardGallery1 = findViewById(R.id.cardGallery1);
        cardGallery2 = findViewById(R.id.cardGallery2);
        cardGallery3 = findViewById(R.id.cardGallery3);

        // Banner Gallery Item
        cardGallery1.setOnClickListener(v -> {
            Toast.makeText(GalleryActivity.this, "Loading Banner presets...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GalleryActivity.this, ProductPresetActivity.class);
            intent.putExtra("PRODUCT_NAME", "Banners");
            startActivity(intent);
        });

        // Business Card Gallery Item
        cardGallery2.setOnClickListener(v -> {
            Toast.makeText(GalleryActivity.this, "Loading Business Card presets...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GalleryActivity.this, ProductPresetActivity.class);
            intent.putExtra("PRODUCT_NAME", "Business Cards");
            startActivity(intent);
        });

        // Sticker Gallery Item
        cardGallery3.setOnClickListener(v -> {
            Toast.makeText(GalleryActivity.this, "Loading Sticker presets...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GalleryActivity.this, ProductPresetActivity.class);
            intent.putExtra("PRODUCT_NAME", "Stickers");
            startActivity(intent);
        });
    }
}