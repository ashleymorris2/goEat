package com.uclan.ashleymorris.goeat.Fragments.Menu;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uclan.ashleymorris.goeat.Adapters.CategoriesListAdapter;
import com.uclan.ashleymorris.goeat.Classes.JSONParser;
import com.uclan.ashleymorris.goeat.Classes.SessionManager;
import com.uclan.ashleymorris.goeat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CategoriesListFragment extends Fragment {

    //URL
    private static final String CATEGORIES_URL =
            "/restaurant-service/scripts/get-menu-categories.php";

    //JSON IDs
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private static final String TAG_CATEGORIES = "categories";//Refers to the array
    private static final String TAG_CATEGORY = "category"; //Refers to elements in the array

    private JSONArray categories;
    private ProgressDialog progressDialog;
    private String [] categoriesList;

    private SessionManager session;

    private ListView listView;

    private TextView textName, textCloseTime;

    public CategoriesListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_categories, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        session = new SessionManager(getActivity());
        listView = (ListView) getActivity().findViewById(R.id.listView_categories);

        textName = (TextView) getActivity().findViewById(R.id.text_restaurant_name);
        textCloseTime = (TextView) getActivity().findViewById(R.id.text_closetime);

        textName.setText(session.getRestaurantName());
        textCloseTime.setText("Closes at: " +session.getCloseTime());

        new LoadCategories().execute();

    }

    private class LoadCategories extends AsyncTask<Void, Void, String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Just a second...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        /**
         * Processes the database access in a background thread
         *
         * @return Null if the JSON request returned 0. An array of strings if the request was a
         * success.
         */
        @Override
        protected String[] doInBackground(Void... voids) {

            //Concatenate the stored ip address that the url together.
            String url = session.getServerIp()+CATEGORIES_URL;

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonResponse = jsonParser.getJSONFromUrl(url);

            try{
                int successCode = jsonResponse.getInt(TAG_SUCCESS);

                //1 means that data has been returned ok.
                if (successCode == 1) {

                    categories = jsonResponse.getJSONArray(TAG_CATEGORIES);

                    //Makes categories list the same length as the JSON array
                    categoriesList = new String[categories.length()];

                    for (int i = 0; i < categories.length(); i++) {

                        JSONObject current = categories.getJSONObject(i);

                        //Gets the individual elements from the JSON array and stores them in the String
                        //array.
                        categoriesList[i] = current.getString(TAG_CATEGORY);
                    }
                }
                else{
                    String message = jsonResponse.getString(TAG_MESSAGE);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    return null;
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return categoriesList;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

            progressDialog.cancel();

            if(strings != null){
                //Can update the listview
                populateListView(strings);
            }

        }
    }

    private void populateListView(final String [] categoriesList){

        //Configure the adapter
        CategoriesListAdapter adapter = new CategoriesListAdapter(getActivity(), categoriesList);

        //Remove the header and footer
        listView.addFooterView(new View(getActivity()), null, false);
        listView.addHeaderView(new View(getActivity()), null, false);

        //Pass the adapter to the listview
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicker, int position,
                                    long id) {

                String itemClicked = categoriesList[position-1];

                //Bundle the itemclicked string as an argument to send to the next fragment
                Bundle arguments = new Bundle();
                arguments.putString("ITEM_CLICKED", itemClicked);

                //Pass the arguments to the fragment
                Fragment fragment = new ItemsListFragment();
                fragment.setArguments(arguments);

                //Commit the transaction
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

    }
}
