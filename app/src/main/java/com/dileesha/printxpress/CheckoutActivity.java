package com.dileesha.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {

    TextView tvOrderSummary, tvTotal;
    RadioGroup rgDelivery, rgPayment;
    Button btnConfirmPayment;
    DatabaseHelper db;

    String productName, quantityString, compiledDetails, currentUserEmail;
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
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        // Fetch data from previous screens
        productName = getIntent().getStringExtra("PRODUCT_NAME");
        quantityString = getIntent().getStringExtra("QUANTITY");
        compiledDetails = getIntent().getStringExtra("COMPILED_DETAILS");

        // 1. Safely convert quantity string to an integer
        try {
            if (quantityString != null && !quantityString.isEmpty()) {
                quantity = Integer.parseInt(quantityString);
            }
        } catch (NumberFormatException e) {
            quantity = 1; // Fallback if something goes wrong
        }

        // 2. Assign dynamic base prices based on the product selected
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

        // Calculate initial total
        calculateTotal();

        tvOrderSummary.setText("Product: " + productName + "\nQuantity: " + quantity + "\n\nSpecs:\n" + compiledDetails.replace(" | ", "\n"));

        // 3. Dynamic Pricing based on delivery method
        rgDelivery.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDelivery) {
                deliveryFee = 150; // Add 150 LKR for home delivery
            } else {
                deliveryFee = 0;
            }
            calculateTotal();
        });

        btnConfirmPayment.setOnClickListener(v -> {
            String deliveryMethod = ((RadioButton) findViewById(rgDelivery.getCheckedRadioButtonId())).getText().toString();
            String paymentMethod = ((RadioButton) findViewById(rgPayment.getCheckedRadioButtonId())).getText().toString();
            String finalTotal = "Rs. " + currentTotal;

            boolean isInserted = db.insertOrder(currentUserEmail, productName, String.valueOf(quantity), compiledDetails, deliveryMethod, paymentMethod, finalTotal);

            if (isInserted) {
                Toast.makeText(CheckoutActivity.this, "PAYMENT CONFIRMED. ORDER SECURED.", Toast.LENGTH_LONG).show();

                // Return to Dashboard and clear the order stack
                Intent intent = new Intent(CheckoutActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CheckoutActivity.this, "Error processing order.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to recalculate total instantly when variables change
    private void calculateTotal() {
        currentTotal = (basePrice * quantity) + deliveryFee;
        tvTotal.setText("Total: Rs. " + currentTotal + ".00");
    }
}