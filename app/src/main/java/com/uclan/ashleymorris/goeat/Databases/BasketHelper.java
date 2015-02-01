package com.uclan.ashleymorris.goeat.Databases;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ashley Morris on 01/02/2015.
 *
 * Constructs a database to the described schema. The basket database will hold all the information
 * about a customers order
 */
public class BasketHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "customerBasket.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "basket";
    private static final String COLUMN_NAME = "item_name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_PRICE = "total_price";

    //The sql statement to create the table
    private static final String DATABAE_CREATE = "CREATE TABLE" +
            TABLE_NAME+ "(" +COLUMN_NAME+ " VARCHAR(150," +COLUMN_QUANTITY+ "INTEGER,"+
            COLUMN_PRICE+ "REAL)";

    public BasketHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(DATABAE_CREATE);
        }
        catch (SQLException ex){
            Log.e("Database creation error: ", ex.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
