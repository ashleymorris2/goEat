package com.uclan.ashleymorris.goeat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.uclan.ashleymorris.goeat.Activities.Authentication.StartActivity;
import com.uclan.ashleymorris.goeat.Classes.SessionManager;
import com.uclan.ashleymorris.goeat.R;


public class SplashActivity extends Activity {

    private boolean isLoggedIn = false;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);
        isLoggedIn = sessionManager.isUserLoggedIn();

        if(isLoggedIn){
            //Show user dashboard
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else{
            //Changes the activity with a 3 second delay
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {

                   Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                   startActivity(intent);

                   finish();
               }
           }, 3000);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
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
