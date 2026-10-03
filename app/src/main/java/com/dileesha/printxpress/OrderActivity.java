package com.dileesha.printxpress;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class OrderActivity extends AppCompatActivity {

    TextView tvOrderTitle, tvOrderSubtitle, tvFileName;
    TextView lblSize, lblMaterial, lblColour, lblSides;
    EditText etQuantity, etCustomText;
    Spinner spinnerSize, spinnerMaterial, spinnerColour, spinnerSides;
    Button btnProceedCheckout, btnSaveDesign, btnUpload;

    DatabaseHelper db;
    String selectedProduct = "", presetName = "", currentUserEmail = "";
    String uploadedFileName = "None";
    private static final int PICK_FILE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        db = new DatabaseHelper(this);
        currentUserEmail = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).getString("LOGGED_IN_EMAIL", "Unknown");

        // Map Views
        tvOrderTitle = findViewById(R.id.tvOrderTitle);
        tvOrderSubtitle = findViewById(R.id.tvOrderSubtitle);
        tvFileName = findViewById(R.id.tvFileName);

        lblSize = findViewById(R.id.lblSize);
        lblMaterial = findViewById(R.id.lblMaterial);
        lblColour = findViewById(R.id.lblColour);
        lblSides = findViewById(R.id.lblSides);

        etQuantity = findViewById(R.id.etQuantity);
        etCustomText = findViewById(R.id.etCustomText);

        spinnerSize = findViewById(R.id.spinnerSize);
        spinnerMaterial = findViewById(R.id.spinnerMaterial);
        spinnerColour = findViewById(R.id.spinnerColour);
        spinnerSides = findViewById(R.id.spinnerSides);

        btnProceedCheckout = findViewById(R.id.btnProceedCheckout);
        btnSaveDesign = findViewById(R.id.btnSaveDesign);
        btnUpload = findViewById(R.id.btnUpload);

        selectedProduct = getIntent().getStringExtra("PRODUCT_NAME");
        presetName = getIntent().getStringExtra("PRESET_NAME");

        if(selectedProduct != null) tvOrderTitle.setText(selectedProduct);
        if(presetName != null) tvOrderSubtitle.setText(presetName);

        setupDynamicSpinners(selectedProduct);

        // 1. Native File Picker
        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // Allows any file type (images, PDFs)
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });

        // 2. Save Design for Later
        btnSaveDesign.setOnClickListener(v -> {
            String details = compileDetails();
            if(db.saveDesign(currentUserEmail, selectedProduct, details)) {
                Toast.makeText(this, "Design Saved Successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save design.", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. Proceed to Checkout
        btnProceedCheckout.setOnClickListener(v -> {
            String quantity = etQuantity.getText().toString().trim();
            if(quantity.isEmpty()) {
                Toast.makeText(OrderActivity.this, "Quantity is required", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(OrderActivity.this, CheckoutActivity.class);
            intent.putExtra("PRODUCT_NAME", selectedProduct);
            intent.putExtra("QUANTITY", quantity);
            intent.putExtra("COMPILED_DETAILS", compileDetails());
            startActivity(intent);
        });
    }

    // Handles the result when a user selects a file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            uploadedFileName = "File attached"; // Fallback

            // Extract the actual file name from the URI
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if(nameIndex != -1) uploadedFileName = cursor.getString(nameIndex);
                cursor.close();
            }
            tvFileName.setText("Attached: " + uploadedFileName);
            tvFileName.setTextColor(android.graphics.Color.parseColor("#00E5FF")); // Turn neon cyan on success
        }
    }

    private String compileDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Preset: ").append(presetName);
        if(spinnerSize.getVisibility() == View.VISIBLE) details.append(" | Size: ").append(spinnerSize.getSelectedItem().toString());
        if(spinnerMaterial.getVisibility() == View.VISIBLE) details.append(" | Material: ").append(spinnerMaterial.getSelectedItem().toString());
        if(spinnerColour.getVisibility() == View.VISIBLE) details.append(" | Colour: ").append(spinnerColour.getSelectedItem().toString());
        if(spinnerSides.getVisibility() == View.VISIBLE) details.append(" | Sides: ").append(spinnerSides.getSelectedItem().toString());

        details.append(" | File: ").append(uploadedFileName);
        details.append(" | Notes: ").append(etCustomText.getText().toString());
        return details.toString();
    }

    private void setupDynamicSpinners(String product) {
        if (product == null) product = "Business Cards";

        String[] sizes; String[] materials; String[] colours; String[] sides;

        switch (product) {
            case "Custom Mugs":
                sizes = new String[]{"11 oz Standard", "15 oz Large"};
                materials = new String[]{"Ceramic", "Magic Color-Changing"};
                // Hide unnecessary fields
                lblSides.setVisibility(View.GONE); spinnerSides.setVisibility(View.GONE);
                lblColour.setVisibility(View.GONE); spinnerColour.setVisibility(View.GONE);
                break;

            case "Custom T-Shirts":
                sizes = new String[]{"Small", "Medium", "Large", "XL", "XXL"};
                materials = new String[]{"100% Cotton", "Polyester Blend"};
                lblSides.setVisibility(View.GONE); spinnerSides.setVisibility(View.GONE);
                lblColour.setVisibility(View.GONE); spinnerColour.setVisibility(View.GONE);
                break;

            case "Stickers":
                sizes = new String[]{"2x2 inch", "3x3 inch", "Custom Die-Cut"};
                materials = new String[]{"Glossy Vinyl", "Matte Paper", "Transparent"};
                lblSides.setVisibility(View.GONE); spinnerSides.setVisibility(View.GONE);
                break;

            default: // Business Cards, Flyers, Posters
                sizes = new String[]{"90 x 54 mm", "85 x 55 mm", "A4", "A5"};
                materials = new String[]{"300 GSM Matte", "300 GSM Gloss", "350 GSM Premium"};
                break;
        }

        colours = new String[]{"Full Colour", "Black & White"};
        sides = new String[]{"Single Sided", "Double Sided"};

        spinnerSize.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sizes));
        spinnerMaterial.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, materials));
        spinnerColour.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, colours));
        spinnerSides.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sides));
    }
}