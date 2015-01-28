package com.uclan.ashleymorris.goeat.Fragments.Menu;



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
public class ItemsListFragment extends Fragment {


    public ItemsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_items, container, false);
    }


}
