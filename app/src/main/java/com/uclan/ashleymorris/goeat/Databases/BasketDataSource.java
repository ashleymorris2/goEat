package com.uclan.ashleymorris.goeat.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uclan.ashleymorris.goeat.Classes.BasketItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashley Morris on 01/02/2015.
 *
 * Class to carry out all crud operations on the basket database.
 *
 * Create a new row = order an individual item.
 * Update a row, modify the quantity and total price for that item
 *
 */
public class BasketDataSource {

    private SQLiteDatabase database;
    private BasketHelper dbHelper;

    public BasketDataSource(Context context) {
        dbHelper = new BasketHelper(context);
    }

    //opens a writeable database
    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    /**
     *
     * @return How many items with a given name have been added to the basket.
     */
    public int getItemCount(String itemName){

        int count = 0;

        String countQuery = "SELECT "+BasketHelper.COLUMN_QUANTITY+ " FROM " +BasketHelper.TABLE_NAME+
                " WHERE " +BasketHelper.COLUMN_NAME+ "=?";

        Cursor cursor = database.rawQuery(countQuery, new String[] {itemName});
        if(cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        return count;
    }

    /**
     *
     * @return True if the table is empty, false if it isn't

     */
    public boolean basketIsEmpty(){

        int count = 0;
        String countQuery = "SELECT count (*) FROM " +BasketHelper.TABLE_NAME;
        Cursor cursor = database.rawQuery(countQuery, null);

        if( cursor.moveToFirst()){
            count = cursor.getInt(0);
        }

        cursor.close();

        if(count > 0){
            return false;
        }
        else {
            return true;
        }
    }

    public void addItemToBasket(String itemName, int quantity, double totalCost){

            ContentValues contentValues = new ContentValues();

            contentValues.put(BasketHelper.COLUMN_NAME, itemName);
            contentValues.put(BasketHelper.COLUMN_QUANTITY, quantity);
            contentValues.put(BasketHelper.COLUMN_PRICE, totalCost);

            database.replace(BasketHelper.TABLE_NAME, null, contentValues);

    }

    /**
     *
     * @return A list of all the items that are currently in the basket.
     */
    public List<BasketItem> getBasketContents(){

        List<BasketItem> basketList = new ArrayList<BasketItem>();

        String query = "SELECT * FROM " +BasketHelper.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);

        //While the cursor is able to move to the next element in the array, read the database
        //contents into the list.
        if(cursor.moveToFirst()){
            do{
                BasketItem item = new BasketItem();

                //Get the cursor to get the coloumn index called "item_name"
                String itemName = cursor.getString(cursor.getColumnIndex(BasketHelper.COLUMN_NAME));
                int itemQuantity = cursor.getInt(cursor.getColumnIndex(BasketHelper.COLUMN_QUANTITY));
                double itemTotalCost = cursor.getDouble(cursor.getColumnIndex(BasketHelper.COLUMN_PRICE));

                item.setItemName(itemName);
                item.setItemQuantity(itemQuantity);
                item.setItemTotalCost(itemTotalCost);

                basketList.add(item);

            }
            while(cursor.moveToNext());
        }

        cursor.close();
        return basketList;
    }


    public void removeItemFromBasket(String itemName){

        database.delete(BasketHelper.TABLE_NAME, BasketHelper.COLUMN_NAME + " =? ", new String []{itemName});
    }

    public void emptyBasket(){

        database.delete(BasketHelper.TABLE_NAME, null, null);
    }

}
