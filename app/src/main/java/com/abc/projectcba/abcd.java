package com.abc.projectcba;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class abcd extends AppCompatActivity {

    Button btn_scan;
    Button connect;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abcd);
        btn_scan = findViewById(R.id.btn_scan);
        connect = findViewById(R.id.connect);
        btn_scan.setOnClickListener( v->
        {
            scanCode();
        });
        connect.setOnClickListener( v->
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

    }

    private void scanCode()
    {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash. Volume down to close flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {

        TextView textView = (TextView) findViewById(R.id.textView);
        if(result.getContents() != null) {
            String data = result.getContents();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Result");
            builder.setMessage(data);
            textView.setText(data);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
}