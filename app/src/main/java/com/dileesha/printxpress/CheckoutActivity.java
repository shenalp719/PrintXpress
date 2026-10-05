package com.dileesha.printxpress;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {

    TextView tvOrderSummary, tvTotal;
    RadioGroup rgDelivery, rgPayment;
    RadioButton rbCard, rbCOD, rbStore, rbDelivery, rbPickup;
    LinearLayout llAddressContainer;
    EditText etCheckoutAddress;
    Button btnConfirmPayment;
    DatabaseHelper db;

    String productName, quantityString, compiledDetails, currentUserEmail;
    String savedName = "", savedPhone = "", savedAddress = "";
    int basePrice = 0;
    int quantity = 1;
    int deliveryFee = 0;
    int currentTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = new DatabaseHelper(this);
        currentUserEmail = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).getString("LOGGED_IN_EMAIL", "Unknown");

        tvOrderSummary = findViewById(R.id.tvOrderSummary);
        tvTotal = findViewById(R.id.tvTotal);
        rgDelivery = findViewById(R.id.rgDelivery);
        rgPayment = findViewById(R.id.rgPayment);
        rbCard = findViewById(R.id.rbCard);
        rbCOD = findViewById(R.id.rbCOD);
        rbStore = findViewById(R.id.rbStore);
        rbDelivery = findViewById(R.id.rbDelivery);
        rbPickup = findViewById(R.id.rbPickup);
        llAddressContainer = findViewById(R.id.llAddressContainer);
        etCheckoutAddress = findViewById(R.id.etCheckoutAddress);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        loadUserData();

        productName = getIntent().getStringExtra("PRODUCT_NAME");
        quantityString = getIntent().getStringExtra("QUANTITY");
        compiledDetails = getIntent().getStringExtra("COMPILED_DETAILS");

        try {
            if (quantityString != null && !quantityString.isEmpty()) quantity = Integer.parseInt(quantityString);
        } catch (NumberFormatException e) {
            quantity = 1;
        }

        if (productName == null) productName = "Unknown Product";
        switch (productName) {
            case "Business Cards": basePrice = 1500; break;
            case "Flyers": basePrice = 2000; break;
            case "Posters": basePrice = 800; break;
            case "Custom Mugs": basePrice = 1200; break;
            case "Custom T-Shirts": basePrice = 2500; break;
            case "Stickers": basePrice = 500; break;
            case "Banners": basePrice = 3500; break;
            default: basePrice = 1000; break;
        }

        calculateTotal();
        tvOrderSummary.setText("Product: " + productName + "\nQuantity: " + quantity + "\n\nSpecs:\n" + compiledDetails.replace(" | ", "\n"));

        // Delivery Logic Guardrails
        rgDelivery.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDelivery) {
                deliveryFee = 150;
                llAddressContainer.setVisibility(View.VISIBLE);

                // Enable COD, disable Store Pay
                rbCOD.setEnabled(true);
                rbCOD.setTextColor(Color.WHITE);
                rbStore.setEnabled(false);
                rbStore.setTextColor(Color.parseColor("#666666")); // Dim text

                // Force switch if invalid selection exists
                if (rbStore.isChecked()) rbCard.setChecked(true);
            } else {
                deliveryFee = 0;
                llAddressContainer.setVisibility(View.GONE);

                // Disable COD, enable Store Pay
                rbCOD.setEnabled(false);
                rbCOD.setTextColor(Color.parseColor("#666666")); // Dim text
                rbStore.setEnabled(true);
                rbStore.setTextColor(Color.WHITE);

                // Force switch if invalid selection exists
                if (rbCOD.isChecked()) rbCard.setChecked(true);
            }
            calculateTotal();
        });

        btnConfirmPayment.setOnClickListener(v -> {
            String addressInput = etCheckoutAddress.getText().toString().trim();

            // Validate Address for Home Delivery
            if (rbDelivery.isChecked()) {
                if (addressInput.isEmpty()) {
                    Toast.makeText(CheckoutActivity.this, "Delivery address is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Save the new address to profile if it changed
                if (!addressInput.equals(savedAddress)) {
                    db.updateProfileDetails(currentUserEmail, savedName, savedPhone, addressInput);
                }
            }

            String deliveryMethod = ((RadioButton) findViewById(rgDelivery.getCheckedRadioButtonId())).getText().toString();
            String paymentMethod = ((RadioButton) findViewById(rgPayment.getCheckedRadioButtonId())).getText().toString();
            String finalTotal = "Rs. " + currentTotal;

            // Optional: Attach address to compiled details for the order history
            String finalDetails = compiledDetails;
            if (rbDelivery.isChecked()) {
                finalDetails += " | Address: " + addressInput;
            }

            boolean isInserted = db.insertOrder(currentUserEmail, productName, String.valueOf(quantity), finalDetails, deliveryMethod, paymentMethod, finalTotal);

            if (isInserted) {
                Toast.makeText(CheckoutActivity.this, "PAYMENT CONFIRMED. ORDER SECURED.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CheckoutActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CheckoutActivity.this, "Error processing order.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        Cursor cursor = db.getUserDetails(currentUserEmail);
        if (cursor.moveToFirst()) {
            savedName = cursor.getString(3) != null ? cursor.getString(3) : "";
            savedAddress = cursor.getString(4) != null ? cursor.getString(4) : "";
            savedPhone = cursor.getString(5) != null ? cursor.getString(5) : "";

            if (!savedAddress.isEmpty()) {
                etCheckoutAddress.setText(savedAddress);
            }
        }
        cursor.close();
    }

    private void calculateTotal() {
        currentTotal = (basePrice * quantity) + deliveryFee;
        tvTotal.setText("Total: Rs. " + currentTotal + ".00");
    }
}