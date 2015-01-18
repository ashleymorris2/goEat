package com.uclan.ashleymorris.goeat.Classes;

/**
 * Created by Ashley Morris on 17/01/2015.
 */
public class StaticVariables {

    //Holds the static string for shared preferences

    private static final String PREF_NAME = "user_data";

    private static final String PREF_LOGIN_NAME = "username";
    private static final String PREF_LOGIN_STATUS = "isLoggedIn";

    public static String getPrefName() {
        return PREF_NAME;
    }

    public static String getPrefLoginName() {
        return PREF_LOGIN_NAME;
    }

    public static String getPrefLoginStatus() {
        return PREF_LOGIN_STATUS;
    }
}
