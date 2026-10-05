package com.dileesha.printxpress;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GalleryActivity extends AppCompatActivity {

    ImageView ivGallery1, ivGallery2, ivGallery3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ivGallery1 = findViewById(R.id.ivGallery1);
        ivGallery2 = findViewById(R.id.ivGallery2);
        ivGallery3 = findViewById(R.id.ivGallery3);

        // Click listeners for the gallery items
        ivGallery1.setOnClickListener(v -> {
            Toast.makeText(GalleryActivity.this, "Loading Banner specifications...", Toast.LENGTH_SHORT).show();
        });

        ivGallery2.setOnClickListener(v -> {
            Toast.makeText(GalleryActivity.this, "Loading Business Card specifications...", Toast.LENGTH_SHORT).show();
        });

        ivGallery3.setOnClickListener(v -> {
            Toast.makeText(GalleryActivity.this, "Loading Sticker specifications...", Toast.LENGTH_SHORT).show();
        });
    }
}