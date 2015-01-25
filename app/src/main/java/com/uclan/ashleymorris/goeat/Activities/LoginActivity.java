package com.uclan.ashleymorris.goeat.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.uclan.ashleymorris.goeat.Classes.JSONParser;
import com.uclan.ashleymorris.goeat.Classes.SessionManager;
import com.uclan.ashleymorris.goeat.R;


public class LoginActivity extends Activity {

    private EditText username, password;
    private Button submitButton;
    private TextView registerButton;
    private ProgressDialog progressDialog;

    JSONParser jsonParser = new JSONParser();
    SessionManager sessionManager;

    //Home IP address, change for when at university:
    private static final String LOGIN_URL =
            "/restaurant-service/scripts/login-script.php";

    //Corresponds to the JSON responses array element tags.
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        //New SessionManager class to store the user data
        sessionManager = new SessionManager(this);
        
        //Link the fields to their xml declarations.
        username = (EditText) findViewById(R.id.edittext_username);
        password = (EditText) findViewById(R.id.edittext_password);

        //Submit button onCLickListener:
        submitButton = (Button) findViewById(R.id.button_login);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginTask loginTask = new LoginTask();
                loginTask.execute();

            }
        });

        //Register button
        registerButton = (TextView) findViewById(R.id.textbutton_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    class LoginTask extends AsyncTask<Void, Void, JSONObject> {

        //Before executing the background stuff (Sets up a progress dialogue to keep users informed)
        @Override
        protected void onPreExecute() {

            //Always call the superclass first
            super.onPreExecute();

            //Display progress dialogue on this screen
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        /*Sends the parameters via HTTP post to the login url, returns JSON data that contains the
        outcome of the login attempt*/
        @Override
        protected JSONObject doInBackground(Void... v) {

            String loginName = username.getText().toString();
            String loginPassword = password.getText().toString();

            //Concatenate the stored ip address that the url together.
            String url = sessionManager.getServerIp()+LOGIN_URL;

            Log.d("URL: ", url);

            //Associative array containing the parameters to pass to the query:
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", loginName));
            params.add(new BasicNameValuePair("password", loginPassword));

            JSONObject jsonResponse = jsonParser.makeHttpRequest(url, HttpPost.METHOD_NAME, params);

            try {
                int successCode = jsonResponse.getInt(TAG_SUCCESS);

                if (successCode == 1) {
                    //Save the user data:
                   sessionManager.saveUserDetails(loginName);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Login attempt", jsonResponse.toString());

            return jsonResponse;
        }

        /*
        Deals with the outcome of the jsonResponse. If success, then the user session is saved an
        they are logged in. If unsuccessful then they will have to re attempt logging in.
         */
        @Override
        protected void onPostExecute(JSONObject jsonResponse) {
            super.onPostExecute(jsonResponse);

            progressDialog.cancel();

            if (jsonResponse != null) {
                //Data has been retrieved
                try {

                    int successCode = jsonResponse.getInt(TAG_SUCCESS);
                    String message = jsonResponse.getString(TAG_MESSAGE);

                    if (successCode == 1) {
                        //Login has been successful
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        //Navigate to new screen:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        //Clears the backstack before starting a new activity.
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        finish();

                    } else {
                        //Unsuccessful login
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                //No data has been returned.
                Toast.makeText(getApplicationContext(),
                        "Connection error. Make sure you have an active network connection and then try again",
                        Toast.LENGTH_LONG).show();
            }


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_login, menu);
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
