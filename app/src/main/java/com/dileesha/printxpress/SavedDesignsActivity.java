package com.dileesha.printxpress;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SavedDesignsActivity extends AppCompatActivity {

    ListView lvSavedDesigns;
    DatabaseHelper db;
    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_designs);

        db = new DatabaseHelper(this);
        currentUserEmail = getSharedPreferences("PrintXpressPrefs", MODE_PRIVATE).getString("LOGGED_IN_EMAIL", "Unknown");

        lvSavedDesigns = findViewById(R.id.lvSavedDesigns);
        loadSavedDesigns();

        // 1. Short-press to Checkout
        lvSavedDesigns.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            String product = cursor.getString(cursor.getColumnIndexOrThrow("PRODUCT"));
            String details = cursor.getString(cursor.getColumnIndexOrThrow("DETAILS"));

            showQuantityDialog(product, details);
        });

        // 2. Long-press to Delete
        lvSavedDesigns.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(this)
                    .setTitle("DELETE_DESIGN")
                    .setMessage("Are you sure you want to permanently delete this design?")
                    .setPositiveButton("CONFIRM", (dialog, which) -> {
                        if (db.deleteSavedDesign(id)) {
                            Toast.makeText(SavedDesignsActivity.this, "Design Deleted", Toast.LENGTH_SHORT).show();
                            loadSavedDesigns(); // Refresh the list
                        } else {
                            Toast.makeText(SavedDesignsActivity.this, "Error deleting design", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();
            return true;
        });
    }

    private void loadSavedDesigns() {
        Cursor cursor = db.getSavedDesigns(currentUserEmail);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No saved designs found.", Toast.LENGTH_SHORT).show();
        }

        String[] fromColumns = {"PRODUCT", "DETAILS"};
        int[] toViews = {R.id.tvDesignProduct, R.id.tvDesignDetails};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item_design,
                cursor,
                fromColumns,
                toViews,
                0
        );
        lvSavedDesigns.setAdapter(adapter);
    }

    private void showQuantityDialog(String product, String details) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ORDER_SAVED_DESIGN");
        builder.setMessage("Enter quantity for " + product + ":");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText("1"); // Default quantity
        builder.setView(input);

        builder.setPositiveButton("PROCEED TO CHECKOUT", (dialog, which) -> {
            String qty = input.getText().toString().trim();
            if(qty.isEmpty() || qty.equals("0")) qty = "1";

            Intent intent = new Intent(SavedDesignsActivity.this, CheckoutActivity.class);
            intent.putExtra("PRODUCT_NAME", product);
            intent.putExtra("QUANTITY", qty);
            intent.putExtra("COMPILED_DETAILS", details);
            startActivity(intent);
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}