package com.uclan.ashleymorris.goeat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uclan.ashleymorris.goeat.Classes.Item;
import com.uclan.ashleymorris.goeat.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Ashley Morris on 29/01/2015.
 */
public class ItemsListAdapter extends ArrayAdapter<Item> {

    private Context context;
    private List<Item> items;

    private boolean basketIsEmpty;

    private TextView textItemName, textItemPrice, textItemDescription, textQuantity;


    public ItemsListAdapter(Context context, List<Item> items, boolean basketIsEmpty) {
        super(context, R.layout.listview_items, items);
        this.context = context;
        this.items = items;
        this.basketIsEmpty = basketIsEmpty;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get the layout inflater service
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = convertView;


        //Make sure that there is a view to work with in case null has been passed
        if (itemView == null) {
            itemView = layoutInflater.inflate(R.layout.listview_items, parent, false);
        }

        //Gets the current item from the array list at the position, so the data can be retrieved.
        final Item currentItem = items.get(position);

        textItemName = (TextView) itemView.findViewById(R.id.text_item_name);
        textItemDescription = (TextView) itemView.findViewById(R.id.text_item_description);
        textItemPrice = (TextView) itemView.findViewById(R.id.text_item_price);
        textQuantity = (TextView) itemView.findViewById(R.id.text_basket_value);

        //Fill the view:
        textItemName.setText(currentItem.getName());
        textItemDescription.setText(currentItem.getDescription());

        //Basket value..
        //If the basket is empty it must be zero, if not query the local database to get a count
        if(basketIsEmpty){
            textQuantity.setText("0 in basket");
        }
        else{
            textQuantity.setText("1 in basket");
        }


        //Use decimal format to add 2 decimal places to the price.
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(2);

        //Finally set the price
        String price = decimalFormat.format(currentItem.getPrice());
        textItemPrice.setText("£" + price);


        return itemView;
    }
}
