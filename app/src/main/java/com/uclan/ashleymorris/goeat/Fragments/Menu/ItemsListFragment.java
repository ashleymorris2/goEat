package com.uclan.ashleymorris.goeat.Fragments.Menu;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uclan.ashleymorris.goeat.Activities.NumberPickerDialogue;
import com.uclan.ashleymorris.goeat.Adapters.ItemsListAdapter;
import com.uclan.ashleymorris.goeat.Classes.Item;
import com.uclan.ashleymorris.goeat.Classes.SessionManager;
import com.uclan.ashleymorris.goeat.Databases.BasketDataSource;
import com.uclan.ashleymorris.goeat.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsListFragment extends Fragment {

    private TextView textCategory;
    private ListView listView;

    private SessionManager session;
    private BasketDataSource basketDatasource;

    private ProgressDialog progressDialog;

    private static String itemCategory;

    private ItemsListAdapter adapter;


    private static final String ITEMS_URL = "/restaurant-service/scripts/get-menu-items.php";
    private static final String TAG_ITEMS = "items";

    public ItemsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_items, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Retrieve the arguments that were sent with the fragment transaction
        Bundle arguments = getArguments();
        itemCategory = arguments.getString("ITEM_CLICKED");

        session = new SessionManager(getActivity());

        listView = (ListView) getActivity().findViewById(R.id.listView_items);

        textCategory = (TextView) getActivity().findViewById(R.id.text_category);
        textCategory.setText(itemCategory);

        LoadItems loadItems = new LoadItems(itemCategory);
        loadItems.execute();
    }

    private class LoadItems extends AsyncTask<Void, Void, List<Item>> {

        boolean basketIsEmpty;

        String itemCategory;
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        /**
         * @param itemCategory
         * The category of the menu items that are to be loaded into the listview and the value that
         * is to be passed with the HTTP method.
         */
        private LoadItems(String itemCategory) {
            this.itemCategory = itemCategory;
            params.add(new BasicNameValuePair("category", itemCategory));
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Just a second...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }


        @Override
        protected List<Item> doInBackground(Void... voids) {

            String url = session.getServerIp() + ITEMS_URL;
            List<Item> menuItemsList;

            try {
                //Open a connection to the url
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                //Add the parameters
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                StatusLine statusLine = httpResponse.getStatusLine();

                //Status 200 = OK, the request has succeeded.
                //Open an input stream for reading the http response
                if(statusLine.getStatusCode() == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    InputStream content = entity.getContent();

                    try {
                        Reader reader = new InputStreamReader(content);

                        //Find the items element in the JSON response
                        JsonParser parser = new JsonParser();
                        JsonObject object = parser.parse(reader).getAsJsonObject();
                        JsonArray itemsJson = object.get(TAG_ITEMS).getAsJsonArray();


                        //Build up the JSON, use GSON to convert to an array of POJOs
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        menuItemsList = Arrays.asList(gson.fromJson(itemsJson, Item[].class));
                        content.close();

                        basketDatasource = new BasketDataSource(getActivity());
                        basketDatasource.open();

                        //Checks the current count of the basket. If it isn't empty the boolean is false
                        //This will mean that each item in the listview will have to check the database
                        //To find out their stored quantity.
                        //If it is empty then it is true and the check won't be made.
                        if(!basketDatasource.basketIsEmpty()){

                            for(int i =0; i < menuItemsList.size(); i++){

                                String name = menuItemsList.get(i).getName();
                                int count= basketDatasource.getItemCount(name);

                                menuItemsList.get(i).setBasketQuantity(count);

                            }
                        }

                        return menuItemsList;

                    }
                    catch (Exception ex){
                        Log.e("ERROR", "JSON error : " + ex.getMessage());
                    }

                }
                else{
                    //Other status code, page load failed
                }

            } catch (UnsupportedEncodingException e) {
                Log.e("ERROR", "Encoding error : " + e.getMessage());
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                Log.e("ERROR", "Protocol error : " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("ERROR", "IO error : " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(List<Item> items) {
            super.onPostExecute(items);

            progressDialog.cancel();

            if(items != null){
                populateListView(items);
                basketDatasource.close();
            }

        }
    }

    private void populateListView(final List<Item> items) {

        //If the adapter is null then need to setup a new one, else the old one is reused and the
        //data is refilled.
        if(listView.getAdapter() == null) {
            adapter = new ItemsListAdapter(getActivity(), items);

            //Pass the adapter to the listview
            listView.setAdapter(adapter);
        }
        else{
            ((ItemsListAdapter) listView.getAdapter()).refill(items);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Item currentItem = items.get(position);

                Intent intent = new Intent(getActivity(), NumberPickerDialogue.class);
                intent.putExtra("ITEM_NAME", currentItem.getName());
                intent.putExtra("ITEM_PRICE", currentItem.getPrice());

                intent.putExtra("ITEM_QUANTITY", currentItem.getBasketQuantity());

                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Refresh the listView if the data was changed in the 2nd activity.
        //Th request code is the identifier for the request. Allows multiple requests to be handled
        //by onActivityResult
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){

                LoadItems loadItems = new LoadItems(itemCategory);
                loadItems.execute();

            }

        }

    }
}
