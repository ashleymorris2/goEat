package com.uclan.ashleymorris.goeat.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
     * @return How many items have been added to the basket.
     */
    public int getItemCount(){

        int count;
        String countQuery = "SELECT * FROM " +BasketHelper.TABLE_NAME;
        Cursor cursor = database.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        return count;
    }
}
