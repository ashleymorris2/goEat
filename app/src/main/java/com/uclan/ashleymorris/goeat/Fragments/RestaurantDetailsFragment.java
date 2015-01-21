package com.uclan.ashleymorris.goeat.Fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uclan.ashleymorris.goeat.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class RestaurantDetailsFragment extends Fragment {


    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }


}
