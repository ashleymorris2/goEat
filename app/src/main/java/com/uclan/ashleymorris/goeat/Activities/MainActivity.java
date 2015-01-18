package com.uclan.ashleymorris.goeat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.uclan.ashleymorris.goeat.Classes.QRParser;
import com.uclan.ashleymorris.goeat.R;

import java.util.HashMap;

public class MainActivity extends Activity {

    private Button buttonScan;
    private TextView textScan, textFormat;
    private HashMap<String, String> barcodeData;

    QRParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Barcode scanner class
        final IntentIntegrator barcodeScanner = new IntentIntegrator(this);

        barcodeData = new HashMap<String, String>();

        textScan = (TextView) findViewById(R.id.text_scan_content);
        textFormat = (TextView) findViewById(R.id.text_scan_format);

        buttonScan = (Button) findViewById(R.id.button_checkin);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                barcodeScanner.initiateScan();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(scanResult != null){
            String scanContent = scanResult.getContents();

            parser = new QRParser(scanContent);
            barcodeData = parser.parseQRcode();

            if(barcodeData != null) {
                textFormat.setText("Restaurant = " + barcodeData.get("restaurant"));
                textScan.setText("Table =  " + barcodeData.get("table"));
            }

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Unsuccessful scan, please try again.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
