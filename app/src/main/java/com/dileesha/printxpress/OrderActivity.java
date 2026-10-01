package com.dileesha.printxpress;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class OrderActivity extends AppCompatActivity {

    TextView tvProductTitle;
    EditText etQuantity, etInstructions;
    Button btnSubmitOrder;
    DatabaseHelper db;
    String selectedProduct = "";
    String currentUserEmail = "testuser@printxpress.com";

    // Notification constants
    private static final String CHANNEL_ID = "PrintXpress_Orders";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        db = new DatabaseHelper(this);
        tvProductTitle = findViewById(R.id.tvProductTitle);
        etQuantity = findViewById(R.id.etQuantity);
        etInstructions = findViewById(R.id.etInstructions);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);

        // 1. Create the notification channel (Required for modern Android)
        createNotificationChannel();

        // 2. Request permission for Android 13+ devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

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

                boolean isInserted = db.insertOrder(currentUserEmail, selectedProduct, quantity, instructions);
                if(isInserted) {
                    // 3. Trigger the notification upon success
                    sendOrderConfirmationNotification(selectedProduct);

                    Toast.makeText(OrderActivity.this, "Order Placed Successfully!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(OrderActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Order Notifications";
            String description = "Channel for order status updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendOrderConfirmationNotification(String product) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Default Android icon for testing
                .setContentTitle("Order Confirmed!")
                .setContentText("Your order for " + product + " is now processing.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}