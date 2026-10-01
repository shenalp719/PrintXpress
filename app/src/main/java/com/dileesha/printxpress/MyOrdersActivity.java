package com.dileesha.printxpress;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MyOrdersActivity extends AppCompatActivity {

    DatabaseHelper db;
    ListView lvOrders;
    ArrayList<String> orderDisplayList;
    ArrayList<String> orderIdList; // Parallel list to keep track of database IDs
    ArrayAdapter<String> adapter;
    String currentUserEmail = "testuser@printxpress.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        db = new DatabaseHelper(this);
        lvOrders = findViewById(R.id.lvOrders);

        loadOrders();

        // Listen for a long click on any list item
        lvOrders.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the hidden database ID for the clicked item
                String clickedOrderId = orderIdList.get(position);
                String fullOrderDetails = orderDisplayList.get(position);

                // Only allow cancellation if it is not already cancelled
                if(fullOrderDetails.contains("Status: Cancelled")) {
                    Toast.makeText(MyOrdersActivity.this, "This order is already cancelled.", Toast.LENGTH_SHORT).show();
                    return true;
                }

                // Show a confirmation popup
                showCancelDialog(clickedOrderId);
                return true;
            }
        });
    }

    private void loadOrders() {
        orderDisplayList = new ArrayList<>();
        orderIdList = new ArrayList<>();
        Cursor cursor = db.getOrders(currentUserEmail);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No orders found.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String orderId = cursor.getString(0);
                String product = cursor.getString(2);
                String quantity = cursor.getString(3);
                String details = cursor.getString(4);
                String status = cursor.getString(5);

                String formattedOrder = "Order #" + orderId + "\n" +
                        "Product: " + product + "\n" +
                        "Quantity: " + quantity + "\n" +
                        "Status: " + status;

                orderDisplayList.add(formattedOrder);
                orderIdList.add(orderId); // Save the ID at the exact same index
            }
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderDisplayList);
        lvOrders.setAdapter(adapter);
    }

    private void showCancelDialog(final String orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Order");
        builder.setMessage("Are you sure you want to cancel Order #" + orderId + "?");

        builder.setPositiveButton("Yes, Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean isCancelled = db.cancelOrder(orderId);
                if(isCancelled) {
                    Toast.makeText(MyOrdersActivity.this, "Order Cancelled", Toast.LENGTH_SHORT).show();
                    loadOrders(); // Refresh the list to show the new status
                } else {
                    Toast.makeText(MyOrdersActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}