package com.uclan.ashleymorris.goeat.Classes;

import java.util.HashMap;

/**
 * Created by Ashley Morris on 18/01/2015.
 *
 * Accepts strings in the format (restaurant::name::table::number)
 */
public class QRParser {

    String inputString; //The string from the QR code that needs to be parsed
    HashMap<String,String> parsedData;

    /**
     *
     * @param inputString Accepts strings in the format (ID::number::restaurant::name::table::number).
     */
    public QRParser(String inputString) {
        this.inputString = inputString;
        parsedData = new HashMap<String, String>();
    }

    /**
     *
     * @return A HashMap containing the parsed QR code split in to tokens. Key and Value.
     */
    public HashMap<String,String> parseQRcode(){

        String delimiter = "[::]+";
        String [] tokens = inputString.split(delimiter);

        try {
            String id = tokens[0];
            String restaurantID = tokens[1];

            String restaurant = tokens[2];
            String restaurantName = tokens[3];

            String table = tokens[4];
            String tableNumber = tokens[5];

            parsedData.put(id, restaurantID);
            parsedData.put(restaurant, restaurantName);
            parsedData.put(table, tableNumber);

            return parsedData;
        }
        catch (ArrayIndexOutOfBoundsException ex){
            //The input string isn't the correct format and isn't able to be parsed so return null
            //Crude protection, if this was a commercial release the check would be better.
            return null;
        }
    }

}
