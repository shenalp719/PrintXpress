package com.dileesha.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductPresetActivity extends AppCompatActivity {

    TextView tvProductTitle, tvPresetName, tvPresetPrice, tvPresetSpecs;
    Button btnSelectPreset;
    String productName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_preset);

        tvProductTitle = findViewById(R.id.tvProductTitle);
        tvPresetName = findViewById(R.id.tvPresetName);
        tvPresetPrice = findViewById(R.id.tvPresetPrice); // Link the price text
        tvPresetSpecs = findViewById(R.id.tvPresetSpecs); // Link the specs text
        btnSelectPreset = findViewById(R.id.btnSelectPreset);

        productName = getIntent().getStringExtra("PRODUCT_NAME");

        if(productName != null) {
            tvProductTitle.setText(productName);
            tvPresetName.setText("Standard " + productName);

            // Dynamically assign price and specs based on product
            int basePrice = 1000;
            String specs = "";

            switch (productName) {
                case "Business Cards":
                    basePrice = 1500;
                    specs = "Size: 90 x 54 mm\nPaper: 300 GSM\nFinish: Matte";
                    break;
                case "Flyers":
                    basePrice = 2000;
                    specs = "Size: A5\nPaper: 150 GSM\nFinish: Gloss";
                    break;
                case "Posters":
                    basePrice = 800;
                    specs = "Size: A3\nPaper: 200 GSM\nFinish: Gloss";
                    break;
                case "Custom Mugs":
                    basePrice = 1200;
                    specs = "Size: 11 oz\nMaterial: Ceramic\nColor: White";
                    break;
                case "Custom T-Shirts":
                    basePrice = 2500;
                    specs = "Size: Medium\nMaterial: 100% Cotton\nColor: Black";
                    break;
                case "Stickers":
                    basePrice = 500;
                    specs = "Size: 2x2 inch\nMaterial: Glossy Vinyl\nCut: Die-Cut";
                    break;
                case "Banners":
                    basePrice = 3500;
                    specs = "Size: 6x3 ft\nMaterial: PVC Flex\nFinish: Matte with Eyelets";
                    break;
                default:
                    basePrice = 1000;
                    specs = "Standard specifications apply.";
                    break;
            }

            tvPresetPrice.setText("Rs. " + basePrice);
            tvPresetSpecs.setText(specs);
        }

        btnSelectPreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductPresetActivity.this, OrderActivity.class);
                intent.putExtra("PRODUCT_NAME", productName);
                intent.putExtra("PRESET_NAME", tvPresetName.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }
}