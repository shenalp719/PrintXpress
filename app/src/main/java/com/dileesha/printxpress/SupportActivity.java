package com.dileesha.printxpress;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SupportActivity extends AppCompatActivity {

    Button btnEmailSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        btnEmailSupport = findViewById(R.id.btnEmailSupport);

        btnEmailSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates an intent to open the user's email app
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:support@printxpress.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PrintXpress Customer Support Request");

                try {
                    startActivity(emailIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SupportActivity.this, "No email client installed on this device.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}