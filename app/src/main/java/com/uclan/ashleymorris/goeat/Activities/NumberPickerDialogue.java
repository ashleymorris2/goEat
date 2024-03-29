package com.uclan.ashleymorris.goeat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uclan.ashleymorris.goeat.Databases.BasketDataSource;
import com.uclan.ashleymorris.goeat.R;

import java.text.DecimalFormat;

public class NumberPickerDialogue extends Activity {

    private TextView itemName, totalCost;

    private Button buttonPlus, buttonMinus, buttonCancel, buttonConfirm;
    private EditText editTextQuantity;
    private BasketDataSource basketDataSource;

    private String name;
    private int currentQuantity;
    private double price, totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogue_number_picker);

        final Intent intent = getIntent();
        basketDataSource = new BasketDataSource(getApplicationContext());

        price = intent.getDoubleExtra("ITEM_PRICE", 0);

        itemName = (TextView) findViewById(R.id.text_item_name);
        name = intent.getStringExtra("ITEM_NAME");
        itemName.setText(name);

        totalCost = (TextView) findViewById(R.id.text_total_cost);
        totalCost.setText("£0.00");

        editTextQuantity = (EditText) findViewById(R.id.editText_quantity);
        editTextQuantity.setText(String.valueOf(intent.getIntExtra("ITEM_QUANTITY", 0)));

        buttonPlus = (Button) findViewById(R.id.button_plus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Update the edit text
                currentQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                if(currentQuantity != 99) {
                    currentQuantity++;
                    editTextQuantity.setText(String.valueOf(currentQuantity));

                    //Update the price
                    totalPrice = price;
                    totalPrice = price * currentQuantity;
                    totalPrice = (double) Math.round(totalPrice * 100) / 100;

                    //Use decimal format to add 2 decimal places to the price.
                    DecimalFormat decimalFormat = new DecimalFormat();
                    decimalFormat.setMinimumFractionDigits(2);
                    totalCost.setText("£" + decimalFormat.format(totalPrice));
                }
            }
        });

        buttonMinus = (Button) findViewById(R.id.button_minus);
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               //Update the edit text
                currentQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                if(currentQuantity != 0) {
                    currentQuantity--;
                    editTextQuantity.setText(String.valueOf(currentQuantity));

                    //Update the price
                    totalPrice = price;
                    totalPrice = price * currentQuantity;
                    totalPrice = (double) Math.round(totalPrice * 100) / 100;

                    //Use decimal format to add 2 decimal places to the price.
                    DecimalFormat decimalFormat = new DecimalFormat();
                    decimalFormat.setMinimumFractionDigits(2);
                    totalCost.setText("£" + decimalFormat.format(totalPrice));
                }
            }
        });

        buttonCancel = (Button) findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonConfirm = (Button) findViewById(R.id.button_confirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basketDataSource.open();

                //Insert or replace the item in the basket if the quantity selected is greater than 0.
                if(currentQuantity >0) {
                    basketDataSource.addItemToBasket(name, currentQuantity, totalPrice);
                }
                //Remove from the basket
                else{
                 basketDataSource.removeItemFromBasket(name);
                }

                basketDataSource.close();

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.number_picker_dialogue, menu);
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
