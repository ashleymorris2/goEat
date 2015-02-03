package com.uclan.ashleymorris.goeat.Activities;

import android.app.Activity;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.uclan.ashleymorris.goeat.Classes.BasketItem;
import com.uclan.ashleymorris.goeat.Databases.BasketDataSource;
import com.uclan.ashleymorris.goeat.R;

import java.text.DecimalFormat;
import java.util.List;

public class BasketActivity extends Activity {

    private BasketDataSource basketDataSource;
    private List<BasketItem> basketItemList;

    private TextView textBasketQuantity, textBasketTotalCost;
    private TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        basketDataSource = new BasketDataSource(getApplicationContext());
        basketDataSource.open();

        basketItemList = basketDataSource.getBasketContents();

        //Work out total cost of the basket and the total contents
        int totalItems = 0;
        double totalCost = 0;
        for(int i = 0; i < basketItemList.size(); i++){

            totalItems = totalItems + basketItemList.get(i).getItemQuantity();
            totalCost = totalCost + basketItemList.get(i).getItemTotalCost();

        }

        totalCost = (double) Math.round(totalCost * 100) / 100;

        textBasketQuantity = (TextView) findViewById(R.id.text_basket_no_of_items);

        if(totalItems > 0) {
            textBasketQuantity.setText("Order total (" + totalItems + " items)");
        }
        else{
            textBasketQuantity.setText("Your basket is empty");
        }

        textBasketTotalCost = (TextView) findViewById(R.id.text_basket_total_cost);

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(2);
        textBasketTotalCost.setText("£"+decimalFormat.format(totalCost));

        if(!basketItemList.isEmpty()) {
            table = (TableLayout) findViewById(R.id.main_table);
            for (int i = 0; i <basketItemList.size(); i++) {

                TableRow tableRow = new TableRow(this);
                tableRow.setPadding(0, 5, 5, 0);

                TextView text = new TextView(this);
                TextView text2 = new TextView(this);


                //If the items quantity is greater than 1 then show the quantity to the user
                if(basketItemList.get(i).getItemQuantity() > 1){
                    text.setText(basketItemList.get(i).getItemName()+" x ["+basketItemList.get(i).getItemQuantity()+"]");
                }
                else {
                    text.setText(basketItemList.get(i).getItemName());
                }

                text.setPadding(0, 0, 96, 0);

                text2.setText(decimalFormat.format(basketItemList.get(i).getItemTotalCost()));
                text2.setGravity(Gravity.RIGHT);

                tableRow.addView(text);
                tableRow.addView(text2);

                table.addView(tableRow);
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
