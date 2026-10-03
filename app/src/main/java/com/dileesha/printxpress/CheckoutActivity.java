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

    String productName, quantity, compiledDetails, currentUserEmail;
    int basePrice = 1500; // Base dummy price
    int currentTotal = 1500;

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

        // Fetch data from OrderActivity
        productName = getIntent().getStringExtra("PRODUCT_NAME");
        quantity = getIntent().getStringExtra("QUANTITY");
        compiledDetails = getIntent().getStringExtra("COMPILED_DETAILS");

        tvOrderSummary.setText("Product: " + productName + "\nQuantity: " + quantity + "\n\nSpecs:\n" + compiledDetails.replace(" | ", "\n"));
        tvTotal.setText("Total: Rs. " + currentTotal + ".00");

        // Dynamic Pricing based on delivery method
        rgDelivery.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDelivery) {
                currentTotal = basePrice + 150; // Add 150 LKR for home delivery
            } else {
                currentTotal = basePrice;
            }
            tvTotal.setText("Total: Rs. " + currentTotal + ".00");
        });

        btnConfirmPayment.setOnClickListener(v -> {
            String deliveryMethod = ((RadioButton) findViewById(rgDelivery.getCheckedRadioButtonId())).getText().toString();
            String paymentMethod = ((RadioButton) findViewById(rgPayment.getCheckedRadioButtonId())).getText().toString();
            String finalTotal = "Rs. " + currentTotal;

            boolean isInserted = db.insertOrder(currentUserEmail, productName, quantity, compiledDetails, deliveryMethod, paymentMethod, finalTotal);

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
}