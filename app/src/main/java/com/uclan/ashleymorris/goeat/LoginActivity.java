package com.uclan.ashleymorris.goeat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Classes.JSONParser;


public class LoginActivity extends Activity {

    private EditText username, password;
    private Button submitButton;
    private TextView registerButton;

    private ProgressDialog progressDialog;

    JSONParser jsonParser = new JSONParser();

    //Home IP address, change for when at university:
    private static final String LOGIN_URL =
            "http://192.168.0.24/restaurant-service/scripts/login-script.php";

    //Corresponds to the JSON responses array element tags.
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Link the fields to their xml declarations.
        username = (EditText) findViewById(R.id.edittext_username);
        password = (EditText) findViewById(R.id.edittext_password);
        submitButton = (Button) findViewById(R.id.button_login);
        registerButton = (TextView) findViewById(R.id.button_register);

        //Submit button onCLickListener:
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginTask loginTask = new LoginTask();
                loginTask.execute();
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

            //Associative array containing the parameters to pass to the query:
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", loginName));
            params.add(new BasicNameValuePair("password", loginPassword));

            JSONObject jsonResponse = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
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

            try {
                int successCode = jsonResponse.getInt(TAG_SUCCESS);
                String message = jsonResponse.getString(TAG_MESSAGE);
                if(successCode == 1){
                    //Login has been successful
                    Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                }
                else {
                    //Unsuccessful login
                    Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
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
