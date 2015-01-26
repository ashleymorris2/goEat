package com.uclan.ashleymorris.goeat.Classes;

/**
 * Created by Ashley Morris on 17/01/2015.
 */
public class StaticVariables {

    //Holds the static strings for shared preferences
    private static final String PREF_NAME = "user_data";

    private static final String PREF_USER_NAME = "username";
    private static final String PREF_LOGIN_STATUS = "isLoggedIn";

    private static final String PREF_RESTAURANT_ID = "restaurant_id";
    private static final String PREF_RESTAURANT_NAME = "restaurant_name";
    private static final String PREF_TABLE_NUMBER = "restaurant_table_number";
    private static final String PREF_PHONENO = "restaurant_phone_number";
    private static final String PREF_OPENTIME = "opening_time";
    private static final String PREF_CLOSETIME = "close_time";
    private static final String PREF_ADDRESS = "restaurant_address";

    private static final String PREF_CHECKIN_STATUS = "isCheckedIn";

    private static final String PREF_IP = "ip_address";


    //==User session:==
    public static String getPrefName() {
        return PREF_NAME;
    }

    public static String getPrefUserName() {
        return PREF_USER_NAME;
    }

    public static String getPrefLoginStatus() {
        return PREF_LOGIN_STATUS;
    }


    //==Check in session:==
    public static String getPrefRestaurantId() {
        return PREF_RESTAURANT_ID;
    }

    public static String getPrefRestaurantName() {
        return PREF_RESTAURANT_NAME;
    }

    public static String getPrefTableNumber() {
        return PREF_TABLE_NUMBER;
    }

    public static String getPrefCheckinStatus() {
        return PREF_CHECKIN_STATUS;
    }

    public static String getPrefPhoneno() {
        return PREF_PHONENO;
    }

    public static String getPrefOpentime() {
        return PREF_OPENTIME;
    }

    public static String getPrefClosetime() {
        return PREF_CLOSETIME;
    }

    public static String getPrefAddress() {
        return PREF_ADDRESS;
    }


    //==System preferences:==
    public static String getPrefIp() {
        return PREF_IP;
    }
}
