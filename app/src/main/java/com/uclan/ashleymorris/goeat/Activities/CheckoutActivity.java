package com.uclan.ashleymorris.goeat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.uclan.ashleymorris.goeat.Classes.SessionManager;
import com.uclan.ashleymorris.goeat.R;

import org.json.JSONException;

import java.math.BigDecimal;

public class CheckoutActivity extends Activity {


    private Button buttonCash, buttonPayPal;
    private TextView textCheckoutTotal;

    private SessionManager sessionManager;

    /**
     * - ENVIRONMENT_SANDBOX to test using a live paypal server but fake credentials
     *
     * - ENVIRONMENT_NO_NETWORK test without communicating with paypals servers
     */
    private static String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static String CLIENT_ID = "AUQxTohlEg66flsLdVKPu-UqfbsU1-UU4h5UYHsx8wP8L3kwcdGyVh3y5e7w9EEHDOq8iipzHDbKdabK";
    private static int REQUEST_CODE_PAYMENT = 1;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CLIENT_ID);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        sessionManager = new SessionManager(this);

        textCheckoutTotal = (TextView) findViewById(R.id.text_checkout_total);
        textCheckoutTotal.setText(sessionManager.getOrderTotalString());

        buttonPayPal = (Button) findViewById(R.id.button_paypal);
        buttonPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BigDecimal totalCost = BigDecimal.valueOf(sessionManager.getOrderTotal());

                PayPalPayment customerOrder = new PayPalPayment(totalCost, "GBP", "Go Eat Order",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent1  = new Intent(getApplication(), PaymentActivity.class);
                intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent1.putExtra(PaymentActivity.EXTRA_PAYMENT, customerOrder);
                startActivityForResult(intent1, REQUEST_CODE_PAYMENT);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if(confirm != null) {
                try {
                    Log.i("PaymentDone", confirm.toJSONObject().toString());

                    //Update in the server.
                    Toast.makeText(this, "PAYMENT SEEMS TO BE OK! BOOM!!!", Toast.LENGTH_LONG).show();

                }
                catch (Exception ex){
                    Log.i("PaymentNotDone", "Dun GOOFED!");
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.check_out, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, PayPalService.class));
    }
}
