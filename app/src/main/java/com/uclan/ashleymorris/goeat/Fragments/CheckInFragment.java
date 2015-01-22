package com.uclan.ashleymorris.goeat.Fragments;


import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.uclan.ashleymorris.goeat.Classes.JSONParser;
import com.uclan.ashleymorris.goeat.Classes.QRParser;
import com.uclan.ashleymorris.goeat.Classes.SessionManager;
import com.uclan.ashleymorris.goeat.R;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CheckInFragment extends Fragment {

    private Button buttonScan;
    private TextView textName;
    private HashMap<String, String> barcodeData;
    private ProgressDialog progressDialog;

    JSONParser jsonParser = new JSONParser();
    QRParser parser;

    SessionManager session;

    //Home IP address, change for when at university:
    private static final String LOGIN_URL =
            "http://192.168.0.24/restaurant-service/system-scripts/checkin-checkout.php";

    //Corresponds to the JSON responses array element tags.
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    public CheckInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_in, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        session = new SessionManager(this.getActivity());

        textName = (TextView) getActivity().findViewById(R.id.text_username);
        textName.setText("Welcome, " +session.getUserName());

        //Barcode scanner class
        final IntentIntegrator barcodeScanner = new IntentIntegrator(this);

        barcodeData = new HashMap<String, String>();

        buttonScan = (Button) getActivity().findViewById(R.id.button_checkin);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                barcodeScanner.initiateScan();

            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //Gets the string that was scanned by the QR reader:
        String scanContent = scanResult.getContents();

        if(scanContent != null){

            //Parses the string into something that we can work with
            parser = new QRParser(scanContent);
            barcodeData = parser.parseQRcode();

            //If the returned barcode data isn't null, a confirmation box
            // will prompt the user to check the check in details.
            if(barcodeData != null) {

                final int id = Integer.parseInt(barcodeData.get("id"));
                final String restaurant = barcodeData.get("restaurant");
                final int table = Integer.parseInt(barcodeData.get("table"));

                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setTitle("Check in?");
                builder.setMessage("Checking in to " +restaurant+" on table " +table+".");
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        CheckInTask checkInTask = new CheckInTask(id, table, restaurant);
                        checkInTask.execute();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        else{
            Toast toast = Toast.makeText(getActivity(), "Unsuccessful scan", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class CheckInTask extends AsyncTask<Void, Void, JSONObject> {

        int id, table;
        String restaurant;

        private CheckInTask(int id, int table, String restaurant) {
            this.id = id;
            this.table = table;
            this.restaurant = restaurant;
        }


        @Override
        protected void onPreExecute() {
            //Before executing the background stuff (Sets up a progress dialogue to keep users informed)

            //Always call the superclass first
            super.onPreExecute();

            //Display progress dialogue on this screen
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Checking in...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            //Associative array containing the parameters to pass to the query:
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("method", "check_in"));
            params.add(new BasicNameValuePair("customer_id", session.getUserName()));
            params.add(new BasicNameValuePair("table_number", Integer.toString(table)));

            JSONObject jsonResponse = jsonParser.makeHttpRequest(LOGIN_URL, HttpPost.METHOD_NAME, params);
            try {

                int successCode = jsonResponse.getInt(TAG_SUCCESS);

                if (successCode == 1) {
                    //Save the user data:
                    //Save this check in to the user session.
                    session.createNewUserSession(id,restaurant,table);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Login attempt", jsonResponse.toString());

            return jsonResponse;
        }

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
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        //Begin fragment change
                        Fragment fragment = new RestaurantDetailsFragment();
                        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_content, fragment);

                        transaction.commit();

                    } else {
                        //Unsuccessful login
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                //No data has been returned.
                Toast.makeText(getActivity(),
                        "Connection error. Make sure you have an active network connection and then try again",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
