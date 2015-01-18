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
     * @param inputString Accepts strings in the format (restaurant::name::table::number).
     */
    public QRParser(String inputString) {
        parsedData = new HashMap<String, String>();
        this.inputString = inputString;
    }

    /**
     *
     * @return A HashMap containing the parsed QR code split in to tokens. Key and Value.
     */
    public HashMap<String,String> parseQRcode(){

        String delimiter = "[::]+";
        String [] tokens = inputString.split(delimiter);

        try {
            String restaurant = tokens[0];
            String restaurantName = tokens[1];
            String table = tokens[2];
            String tableNumber = tokens[3];

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
