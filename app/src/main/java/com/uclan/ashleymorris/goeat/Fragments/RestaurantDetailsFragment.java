package com.uclan.ashleymorris.goeat.Fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uclan.ashleymorris.goeat.Activities.BasketActivity;
import com.uclan.ashleymorris.goeat.Activities.CheckoutActivity;
import com.uclan.ashleymorris.goeat.Activities.MenuActivity;
import com.uclan.ashleymorris.goeat.Classes.JSONParser;
import com.uclan.ashleymorris.goeat.Classes.SessionManager;
import com.uclan.ashleymorris.goeat.Databases.BasketDataSource;
import com.uclan.ashleymorris.goeat.R;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantDetailsFragment extends Fragment {

    JSONParser jsonParser = new JSONParser();

    private SessionManager session;
    private ProgressDialog progressDialog;

    //Home IP address, change for when at university:
    private static final String CHECKOUT_URL =
            "/restaurant-service/scripts/checkin-checkout.php";

    //Corresponds to the JSON responses array element tags.
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private TextView textName, textPhoneNum, textOpenTime, textCloseTime, textAddress;

    private Button buttonCheckout, buttonViewMenu;

    private BasketDataSource basketDataSource;


    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        session = new SessionManager(getActivity());
        basketDataSource = new BasketDataSource(getActivity());
        basketDataSource.open();


        textName = (TextView) getActivity().findViewById(R.id.text_restaurant_name);
        textAddress = (TextView) getActivity().findViewById(R.id.text_address);
        textPhoneNum = (TextView) getActivity().findViewById(R.id.text_phonnumber);
        textOpenTime = (TextView) getActivity().findViewById(R.id.text_opentime);
        textCloseTime = (TextView) getActivity().findViewById(R.id.text_closetime);

        //Update the views with the saved restaurant data
        textName.setText(session.getRestaurantName());
        textAddress.setText(session.getAddress());
        textPhoneNum.setText(session.getPhoneNo());
        textOpenTime.setText("Open: "+session.getOpenTime());
        textCloseTime.setText("Close: "+session.getCloseTime());

        //Button on click listeners:
        buttonCheckout = (Button) getActivity().findViewById(R.id.button_checkout);
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            View v;
            @Override
            public void onClick(View view) {
                //If order place has been set then go to payment screen
                //else If the basket is empty then checkout the user
                //else If the basket has contents then go to basket screen.
                if(session.orderIsPlaced()) {
                    Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                    startActivity(intent);

                }
                else if(basketDataSource.basketIsEmpty()) {
                    CheckOutTask checkOutTask = new CheckOutTask();
                    checkOutTask.execute();
                }
                else{
                    Intent intent = new Intent(getActivity(), BasketActivity.class);
                    startActivity(intent);
                }
            }
        });

        buttonViewMenu = (Button) getActivity().findViewById(R.id.button_viewmenu);

        //If an order has been placed hide the view menu button
        if(session.orderIsPlaced()){
            buttonViewMenu.setVisibility(View.GONE);
        }

        buttonViewMenu.setOnClickListener(new View.OnClickListener() {
           View v;
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MenuActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Do in background attempts to connect to the database and remove the user from the tables table.
     *
     * Post informs user of the outcome and redirects them to the Checkin page.
     */
    private class CheckOutTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Display progress dialogue on this screen
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Just a second...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            String url = session.getServerIp()+CHECKOUT_URL;

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("method", "check_out"));
            params.add(new BasicNameValuePair("table_number", Integer.toString(session.getTableNum())));

            JSONObject jsonResponse = jsonParser.makeHttpRequest(url, HttpPost.METHOD_NAME, params);

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

                        //Clears the user check-in data, clearing them from the restaurant tables database
                        session.checkOutUser();

                        //Checkout has been successful
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        //Begin fragment change
                        Fragment fragment = new CheckInFragment();
                        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_content, fragment);

                        transaction.commit();

                    } else {
                        //Unsuccessful checkout
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
