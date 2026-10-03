package com.dileesha.printxpress;

import android.database.Cursor;
import android.os.Bundle;
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

        // Long-press to delete a design
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

        // Map database columns to the XML text views
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
}