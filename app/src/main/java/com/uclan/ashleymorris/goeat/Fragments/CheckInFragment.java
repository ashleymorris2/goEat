package com.uclan.ashleymorris.goeat.Fragments;



import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.uclan.ashleymorris.goeat.Classes.QRParser;
import com.uclan.ashleymorris.goeat.Classes.SessionManager;
import com.uclan.ashleymorris.goeat.R;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CheckInFragment extends Fragment {

    private Button buttonScan;
    private TextView textName;
    private HashMap<String, String> barcodeData;

    QRParser parser;
    SessionManager session;


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
                final String table = barcodeData.get("table");

                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setTitle("Check in?");
                builder.setMessage("Checking in to " +restaurant+" on table " +table+".");
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Save this check in to the user session.
                        session.createNewUserSession(id,restaurant,table);

                        //Begin fragment change
                        Fragment fragment = new RestaurantDetailsFragment();
                        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_content, fragment);

                        transaction.commit();
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
            Toast toast = Toast.makeText(this.getActivity(), "Unsuccessful scan", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
