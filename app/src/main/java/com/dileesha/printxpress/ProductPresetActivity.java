package com.dileesha.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductPresetActivity extends AppCompatActivity {

    TextView tvProductTitle, tvPresetName;
    Button btnSelectPreset;
    String productName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_preset);

        tvProductTitle = findViewById(R.id.tvProductTitle);
        tvPresetName = findViewById(R.id.tvPresetName);
        btnSelectPreset = findViewById(R.id.btnSelectPreset);

        // Get the product tapped on the Dashboard
        productName = getIntent().getStringExtra("PRODUCT_NAME");
        if(productName != null) {
            tvProductTitle.setText(productName);
            tvPresetName.setText("Standard " + productName);
        }

        btnSelectPreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass BOTH the product name and preset name to the customization screen
                Intent intent = new Intent(ProductPresetActivity.this, OrderActivity.class);
                intent.putExtra("PRODUCT_NAME", productName);
                intent.putExtra("PRESET_NAME", tvPresetName.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }
}