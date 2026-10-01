package com.dileesha.printxpress;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SupportActivity extends AppCompatActivity {

    Button btnContactSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        btnContactSupport = findViewById(R.id.btnContactSupport);

        btnContactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates an intent to send an email
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:support@printxpress.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Design Support Inquiry");

                try {
                    startActivity(emailIntent);
                } catch (Exception e) {
                    Toast.makeText(SupportActivity.this, "No email app found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}