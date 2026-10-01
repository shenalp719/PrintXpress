package com.dileesha.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrderActivity extends AppCompatActivity {

    TextView tvProductTitle;
    EditText etQuantity, etInstructions;
    Button btnSubmitOrder;
    DatabaseHelper db;
    String selectedProduct = "";
    String currentUserEmail = "testuser@printxpress.com"; // Hardcoded for now, we will pass actual email later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        db = new DatabaseHelper(this);

        tvProductTitle = findViewById(R.id.tvProductTitle);
        etQuantity = findViewById(R.id.etQuantity);
        etInstructions = findViewById(R.id.etInstructions);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);

        // Get the product name passed from the Dashboard
        selectedProduct = getIntent().getStringExtra("PRODUCT_NAME");
        if(selectedProduct != null) {
            tvProductTitle.setText("Order: " + selectedProduct);
        }

        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = etQuantity.getText().toString().trim();
                String instructions = etInstructions.getText().toString().trim();

                if(quantity.isEmpty() || instructions.isEmpty()) {
                    Toast.makeText(OrderActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save to Database
                boolean isInserted = db.insertOrder(currentUserEmail, selectedProduct, quantity, instructions);
                if(isInserted) {
                    Toast.makeText(OrderActivity.this, "Order Placed Successfully!", Toast.LENGTH_LONG).show();
                    finish(); // Go back to Dashboard
                } else {
                    Toast.makeText(OrderActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}