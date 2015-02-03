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

    public static final String TABLE_NAME = "basket";

    public static final String COLUMN_NAME = "item_name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_PRICE = "total_price";

    //The sql statement to create the table
    private static final String DATABASE_CREATE = "CREATE TABLE " +
            TABLE_NAME+ "(" +COLUMN_NAME+ " VARCHAR(150), " +COLUMN_QUANTITY+ " INTEGER, "+
            COLUMN_PRICE+ " REAL, PRIMARY KEY("+COLUMN_NAME+"));";

    public BasketHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(DATABASE_CREATE);
        }
        catch (SQLException ex){
            Log.e("Database creation error: ", ex.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
