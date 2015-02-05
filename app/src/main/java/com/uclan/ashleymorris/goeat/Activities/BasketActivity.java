package com.uclan.ashleymorris.goeat.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uclan.ashleymorris.goeat.Classes.BasketItem;
import com.uclan.ashleymorris.goeat.Classes.JSONParser;
import com.uclan.ashleymorris.goeat.Classes.SessionManager;
import com.uclan.ashleymorris.goeat.Databases.BasketDataSource;
import com.uclan.ashleymorris.goeat.R;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BasketActivity extends Activity {

    private BasketDataSource basketDataSource;
    private List<BasketItem> basketItemList;

    private SessionManager sessionManager;

    private TextView textBasketQuantity, textBasketTotalCost, textReciptTotalCost;
    private TableLayout table;
    private Button buttonPlaceOrder;

    private JSONParser jsonParser = new JSONParser();
    private ProgressDialog progressDialog;

    private int totalItems = 0;
    private double totalCost = 0;

    private static final String ADD_ORDER_URL = "/restaurant-service/scripts/add-order-script.php";

    //Corresponds to the JSON responses array element tags.
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);


        basketDataSource = new BasketDataSource(getApplicationContext());
        basketDataSource.open();

        /*
         * Get the basket contents and store them in an object
         * so that it is easier to work with.
         */
        basketItemList = basketDataSource.getBasketContents();

        sessionManager = new SessionManager(getApplicationContext());


        //Work out total cost of the basket and the total contents
        for (int i = 0; i < basketItemList.size(); i++) {

            totalItems = totalItems + basketItemList.get(i).getItemQuantity();
            totalCost = totalCost + basketItemList.get(i).getItemTotalCost();
        }

        totalCost = (double) Math.round(totalCost * 100) / 100;

        //Update the UI
        textBasketQuantity = (TextView) findViewById(R.id.text_basket_no_of_items);
        if (totalItems > 0) {
            textBasketQuantity.setText("Order total (" + totalItems + " items)");
        } else {
            textBasketQuantity.setText("Your basket is empty");
        }

        textBasketTotalCost = (TextView) findViewById(R.id.text_basket_total_cost);
        textReciptTotalCost = (TextView) findViewById(R.id.text_receipt_total_cost);


        //Use decimal format to set the double to 2 decimal places
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(2);
        textBasketTotalCost.setText("£" + decimalFormat.format(totalCost));
        textReciptTotalCost.setText("£" + decimalFormat.format(totalCost));

        buttonPlaceOrder = (Button) findViewById(R.id.button_place_order);
        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceOrderTask placeOrderTask = new PlaceOrderTask();
                placeOrderTask.execute();
            }
        });


        //Table layout..
        //Display if the basket isn't empty
        if (!basketItemList.isEmpty()) {

            table = (TableLayout) findViewById(R.id.main_table);

            //Programatically define the table layout and rows.
            for (int i = 0; i < basketItemList.size(); i++) {

                TableRow tableRow = new TableRow(this);
                tableRow.setPadding(0, 4, 0, 4);

                //Table row params with a gravity of 1, fills the row. Passed to the linearLayout
                TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

                //Create two new textViews to hold the text
                TextView text = new TextView(this);
                TextView text2 = new TextView(this);

                //Linear layout to hold the textViews
                LinearLayout linearLayout = new LinearLayout(this);

                linearLayout.setLayoutParams(params);
                linearLayout.setWeightSum(2f);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                //Linear layout params that are passed to the textViews.
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 0.5f);

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1.5f);

                params1.gravity = Gravity.RIGHT;

                text.setLayoutParams(params1);
                text2.setLayoutParams(params2);

                //If the items quantity is greater than 1 then show the quantity to the user
                if (basketItemList.get(i).getItemQuantity() > 1) {
                    text.setText(basketItemList.get(i).getItemName() + " x [" + basketItemList.get(i).getItemQuantity() + "]");
                } else {
                    text.setText(basketItemList.get(i).getItemName());
                }

                text2.setText(decimalFormat.format(basketItemList.get(i).getItemTotalCost()));
                text2.setGravity(Gravity.RIGHT);

                linearLayout.addView(text);
                linearLayout.addView(text2);
                tableRow.addView(linearLayout);

                table.addView(tableRow);
            }
        }
        else{
            /*
             * And if the table is empty then simply make the view containing the table disappear           */
            RelativeLayout mainTable = (RelativeLayout) findViewById(R.id.table_contents_holder);
            mainTable.setVisibility(View.GONE);
        }
    }

    private class PlaceOrderTask extends AsyncTask<Void, Void, JSONObject> {

        Gson gson = new Gson();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Display progress dialogue on this screen
            progressDialog = new ProgressDialog(BasketActivity.this);
            progressDialog.setMessage("Placing order...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            //Get the httpPost parameters
            //Serialize the basket contents into a JSON string to store in the database
            String basketJson = gson.toJson(basketItemList);
            String userName = sessionManager.getUserName();
            int tableNumber = sessionManager.getTableNum();

            String url = sessionManager.getServerIp() + ADD_ORDER_URL;

            //Associative array containing the parameters to pass to the query:
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("customer_id", userName));
            params.add(new BasicNameValuePair("items_info", basketJson));
            params.add(new BasicNameValuePair("order_total", String.valueOf(totalCost)));
            params.add(new BasicNameValuePair("table_number", String.valueOf(tableNumber)));


            return jsonParser.makeHttpRequest(url, HttpPost.METHOD_NAME, params);
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

                        //Order has been successful
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();


                        //Navigate to payment screen next. Which will open a paypal intent
                        Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);

                        //Add flags to the intent to remove the menu from the back stack
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                |Intent.FLAG_ACTIVITY_NEW_TASK);


                        //Set the session to order being placed.
                        //Saves the total to shared preferences
                        //Clears the basket database.
                        sessionManager.setOrderPlaced(true);
                        sessionManager.setOrderTotal(totalCost);
                        basketDataSource.emptyBasket();

                        startActivity(intent);

                    }
                    else {
                        //Unsuccessful order
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
        getMenuInflater().inflate(R.menu.basket, menu);
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
