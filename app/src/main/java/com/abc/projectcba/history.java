package com.abc.projectcba;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class history extends AppCompatActivity {

    TextView tv3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        tv3 = findViewById(R.id.tv3);

        String scannedData = getIntent().getStringExtra("scannedData");
        tv3.setText( "Device Name: " + scannedData);

    }
}