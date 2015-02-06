package com.uclan.ashleymorris.goeat;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uclan.ashleymorris.goeat.classes.SessionManager;

//This activity is for debug and testing purposes only. It allows the server ip address that the app uses to be
//changed.

public class debugSettingsActivity extends Activity {

    Button buttonSave;
    EditText ipAddress;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipaddress_settings);

        session = new SessionManager(this);
        ipAddress = (EditText) findViewById(R.id.edittext_ipAddress);

        buttonSave = (Button) findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = ipAddress.getText().toString();
                session.setServerIp(address);
                Toast.makeText(getApplicationContext(), "IP Address setting changed to: " +address ,
                        Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ipaddress_settings, menu);
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
