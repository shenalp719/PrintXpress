package com.dileesha.printxpress;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyOrdersActivity extends AppCompatActivity {

    DatabaseHelper db;
    ListView lvOrders;
    BottomNavigationView bottomNavigationView;
    ArrayList<OrderModel> orderList;
    OrderAdapter adapter;
    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        currentUserEmail = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).getString("LOGGED_IN_EMAIL", "Unknown User");
        db = new DatabaseHelper(this);
        lvOrders = findViewById(R.id.lvOrders);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Remove the default list dividers since our cyber cards have margins
        lvOrders.setDivider(null);
        lvOrders.setDividerHeight(0);

        loadOrders();

        lvOrders.setOnItemLongClickListener((parent, view, position, id) -> {
            OrderModel clickedOrder = orderList.get(position);

            if(clickedOrder.status.equalsIgnoreCase("Cancelled")) {
                Toast.makeText(MyOrdersActivity.this, "This order is already cancelled.", Toast.LENGTH_SHORT).show();
                return true;
            }

            showCancelDialog(clickedOrder.id);
            return true;
        });

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_orders);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(MyOrdersActivity.this, DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_orders) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MyOrdersActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadOrders() {
        orderList = new ArrayList<>();
        Cursor cursor = db.getOrders(currentUserEmail);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No orders found.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                // Corrected Column Indexes for Database V5
                String orderId = cursor.getString(0);
                String product = cursor.getString(2);
                String qty = cursor.getString(3);
                String delivery = cursor.getString(5);
                String total = cursor.getString(7);
                String status = cursor.getString(8);

                orderList.add(new OrderModel(orderId, product, qty, delivery, total, status));
            }
        }
        cursor.close();

        adapter = new OrderAdapter(this, orderList);
        lvOrders.setAdapter(adapter);
    }

    private void showCancelDialog(final String orderId) {
        new AlertDialog.Builder(this)
                .setTitle("TERMINATE_ORDER")
                .setMessage("Are you sure you want to cancel Order #" + orderId + "?")
                .setPositiveButton("CONFIRM_CANCEL", (dialog, which) -> {
                    if(db.cancelOrder(orderId)) {
                        Toast.makeText(MyOrdersActivity.this, "Order Cancelled", Toast.LENGTH_SHORT).show();
                        loadOrders(); // Refresh the list UI
                    } else {
                        Toast.makeText(MyOrdersActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("ABORT", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // --- Custom Object & Adapter for Cyberpunk UI ---

    static class OrderModel {
        String id, product, qty, delivery, total, status;
        OrderModel(String id, String product, String qty, String delivery, String total, String status) {
            this.id = id; this.product = product; this.qty = qty;
            this.delivery = delivery; this.total = total; this.status = status;
        }
    }

    class OrderAdapter extends ArrayAdapter<OrderModel> {
        public OrderAdapter(Context context, List<OrderModel> orders) {
            super(context, 0, orders);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_order, parent, false);
            }

            OrderModel order = getItem(position);

            TextView tvOrderId = convertView.findViewById(R.id.tvOrderId);
            TextView tvOrderStatus = convertView.findViewById(R.id.tvOrderStatus);
            TextView tvOrderProduct = convertView.findViewById(R.id.tvOrderProduct);
            TextView tvOrderSummary = convertView.findViewById(R.id.tvOrderSummary);
            TextView tvOrderTotal = convertView.findViewById(R.id.tvOrderTotal);

            tvOrderId.setText("ORDER #" + order.id);
            tvOrderProduct.setText(order.product);
            tvOrderSummary.setText("Qty: " + order.qty + " | " + order.delivery);

            // Format Total display (handle if the DB already contains "Rs.")
            if (order.total != null && order.total.startsWith("Rs.")) {
                tvOrderTotal.setText(order.total);
            } else {
                tvOrderTotal.setText("Total: Rs. " + order.total);
            }

            tvOrderStatus.setText(order.status.toUpperCase());

            // Change status text color based on state
            if (order.status.equalsIgnoreCase("Cancelled")) {
                tvOrderStatus.setTextColor(Color.parseColor("#FF003C")); // Cyber Pink
            } else {
                tvOrderStatus.setTextColor(Color.parseColor("#00E5FF")); // Cyber Cyan
            }

            return convertView;
        }
    }
}