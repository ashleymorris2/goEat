package com.uclan.ashleymorris.goeat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uclan.ashleymorris.goeat.R;


/**
 * Created by Ashley Morris on 25/01/2015.
 */
public class CategoriesListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String [] categories;

    public CategoriesListAdapter(Context context, String[] categories) {
        super(context, R.layout.listview_categories, categories);
        this.context = context;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get the layout inflater service to
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = convertView;

        //Make sure that there is a view to work with in case null has been passed
        if(itemView == null){
            itemView = layoutInflater.inflate(R.layout.listview_categories, parent, false);
        }

        //Get the string to work with that is at the current position
        final String category = categories[position];
        TextView textCategory = (TextView) itemView.findViewById(R.id.item_text_category);

        //Fill and populate the view:
        textCategory.setText(category);

        return itemView;
    }
}
