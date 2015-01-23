package com.uclan.ashleymorris.goeat.Classes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ashley Morris on 17/01/2015.
 */
public class SessionManager {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    /**
     *
     * @param context The calling application context.
     */
    public SessionManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(StaticVariables.getPrefName(), 0);
        this.editor = sharedPreferences.edit();
    }


    /**
     * Stores a username in sharedPreferences and sets a boolean flag to true. Indicating that the user
     * has logged in.
     *
     * @param username Username of the logged in user.
     */
    public void saveUserDetails(String username){
        editor.putString(StaticVariables.getPrefUserName(), username);
        editor.putBoolean(StaticVariables.getPrefLoginStatus(), true);
        editor.commit();
    }


    /**
     * Stores a user session. When they check into a restaurant. This is in case they close the app
     * while they are still showing on the remote system.
     *
     * @param ID The unique id that identifies the restaurant that the user ic checking into.
     * @param restaurantName The restaurant that the user has checked in to.
     * @param tableNumber The table number that the user is on.
     */
    public void createNewUserSession(int ID, String restaurantName, int tableNumber){

        editor.putInt(StaticVariables.getPrefRestaurantId(), ID);
        editor.putString(StaticVariables.getPrefRestaurantName(),restaurantName);
        editor.putInt(StaticVariables.getPrefTableNumber(), tableNumber);

        editor.putBoolean(StaticVariables.getPrefCheckinStatus(), true);
        editor.commit();
    }

    /**
     * @return A boolean representing whether the user has checked into a restaurant or not.
     */
    public boolean isUserCheckedIn(){
        return this.sharedPreferences.getBoolean(StaticVariables.getPrefCheckinStatus(), false);
    }


    /**
     * @return A boolean. True if logged in, false if not.
     */
    public boolean isUserLoggedIn(){
        return this.sharedPreferences.getBoolean(StaticVariables.getPrefLoginStatus(), false);
    }

    public String getUserName(){
        return this.sharedPreferences.getString(StaticVariables.getPrefUserName(), null);
    }

    public void checkOutUser(){
        editor.remove(StaticVariables.getPrefCheckinStatus());
        editor.remove(StaticVariables.getPrefTableNumber());
        editor.remove(StaticVariables.getPrefRestaurantId());
        editor.remove(StaticVariables.getPrefRestaurantName());

        editor.commit();
    }


    public void logOutUser(){
        editor.clear();
        editor.commit();
    }
}
