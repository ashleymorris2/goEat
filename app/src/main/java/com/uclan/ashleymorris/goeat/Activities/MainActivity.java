package com.uclan.ashleymorris.goeat.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import com.uclan.ashleymorris.goeat.Classes.SessionManager;
import com.uclan.ashleymorris.goeat.Fragments.CheckInFragment;
import com.uclan.ashleymorris.goeat.Fragments.RestaurantDetailsFragment;
import com.uclan.ashleymorris.goeat.R;

import java.util.HashMap;

public class MainActivity extends Activity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = null;
        session = new SessionManager(this);

       if(!session.isUserCheckedIn()){
           //If the user isn't checked in show this screen:
           fragment = new CheckInFragment();
       }
        else{
           //If not show this screen:
           fragment = new RestaurantDetailsFragment();
       }

        getFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
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
