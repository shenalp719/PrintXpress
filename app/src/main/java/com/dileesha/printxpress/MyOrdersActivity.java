package com.dileesha.printxpress;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MyOrdersActivity extends AppCompatActivity {

    DatabaseHelper db;
    ListView lvOrders;
    ArrayList<String> orderList;
    ArrayAdapter<String> adapter;
    String currentUserEmail = "testuser@printxpress.com"; // Hardcoded matching the OrderActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        db = new DatabaseHelper(this);
        lvOrders = findViewById(R.id.lvOrders);
        orderList = new ArrayList<>();

        loadOrders();
    }

    private void loadOrders() {
        Cursor cursor = db.getOrders(currentUserEmail);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No orders found.", Toast.LENGTH_SHORT).show();
        } else {
            // Loop through all results in the database
            while (cursor.moveToNext()) {
                // We extract data based on the column index (0 is ID, 1 is Email, 2 is Product, etc.)
                String orderId = cursor.getString(0);
                String product = cursor.getString(2);
                String quantity = cursor.getString(3);
                String details = cursor.getString(4);
                String status = cursor.getString(5);

                // Format how it will look in the list
                String formattedOrder = "Order #" + orderId + "\n" +
                        "Product: " + product + "\n" +
                        "Quantity: " + quantity + "\n" +
                        "Instructions: " + details + "\n" +
                        "Status: " + status;

                orderList.add(formattedOrder);
            }
        }

        // Always close your cursors to prevent memory leaks!
        cursor.close();

        // The adapter bridges our ArrayList of strings to the visual XML ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderList);
        lvOrders.setAdapter(adapter);
    }
}