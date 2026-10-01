package com.dileesha.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    Button btnBusinessCards, btnFlyers, btnMugs, btnTshirts, btnMyOrders, btnSupport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnBusinessCards = findViewById(R.id.btnBusinessCards);
        btnFlyers = findViewById(R.id.btnFlyers);
        btnMugs = findViewById(R.id.btnMugs);
        btnTshirts = findViewById(R.id.btnTshirts);
        btnMyOrders = findViewById(R.id.btnMyOrders);
        onCreate: btnSupport = findViewById(R.id.btnSupport);

        btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, SupportActivity.class);
                startActivity(intent);
            }
        });

        View.OnClickListener productClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String productName = clickedButton.getText().toString();

                // Open OrderActivity and pass the product name
                Intent intent = new Intent(DashboardActivity.this, OrderActivity.class);
                intent.putExtra("PRODUCT_NAME", productName);
                startActivity(intent);
            }
        };

        btnBusinessCards.setOnClickListener(productClickListener);
        btnFlyers.setOnClickListener(productClickListener);
        btnMugs.setOnClickListener(productClickListener);
        btnTshirts.setOnClickListener(productClickListener);

        btnMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MyOrdersActivity.class);
                startActivity(intent);
            }
        });
    }
}