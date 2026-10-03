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

    TextView tvOrderTitle, tvOrderSubtitle;
    EditText etQuantity, etSize, etMaterial, etColour, etSides, etCustomText;
    Button btnSubmitOrder, btnUpload;
    DatabaseHelper db;
    String selectedProduct = "";
    String presetName = "";
    String currentUserEmail = "";

    private static final String CHANNEL_ID = "PrintXpress_Orders";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        db = new DatabaseHelper(this);
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("LOGGED_IN_EMAIL", "Unknown User");

        tvOrderTitle = findViewById(R.id.tvOrderTitle);
        tvOrderSubtitle = findViewById(R.id.tvOrderSubtitle);
        etQuantity = findViewById(R.id.etQuantity);
        etSize = findViewById(R.id.etSize);
        etMaterial = findViewById(R.id.etMaterial);
        etColour = findViewById(R.id.etColour);
        etSides = findViewById(R.id.etSides);
        etCustomText = findViewById(R.id.etCustomText);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);
        btnUpload = findViewById(R.id.btnUpload);

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        selectedProduct = getIntent().getStringExtra("PRODUCT_NAME");
        presetName = getIntent().getStringExtra("PRESET_NAME");

        if(selectedProduct != null) tvOrderTitle.setText(selectedProduct);
        if(presetName != null) tvOrderSubtitle.setText(presetName);

        btnUpload.setOnClickListener(v -> Toast.makeText(OrderActivity.this, "Artwork upload module initializing...", Toast.LENGTH_SHORT).show());

        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = etQuantity.getText().toString().trim();

                if(quantity.isEmpty()) {
                    Toast.makeText(OrderActivity.this, "Quantity is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Compile all customization fields into one detailed string for the database
                String compiledDetails = "Preset: " + presetName +
                        " | Size: " + etSize.getText().toString() +
                        " | Material: " + etMaterial.getText().toString() +
                        " | Colour: " + etColour.getText().toString() +
                        " | Sides: " + etSides.getText().toString() +
                        " | Design Text: " + etCustomText.getText().toString();

                boolean isInserted = db.insertOrder(currentUserEmail, selectedProduct, quantity, compiledDetails);
                if(isInserted) {
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
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Order Confirmed!")
                .setContentText("Your order for " + product + " is now processing.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}