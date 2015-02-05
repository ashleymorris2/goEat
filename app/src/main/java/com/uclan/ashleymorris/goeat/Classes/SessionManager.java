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

    final static int AWAITING_PAYMENT = 0;


    /**
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
    public void saveUserDetails(String username) {
        editor.putString(StaticVariables.getPrefUserName(), username);
        editor.putBoolean(StaticVariables.getPrefLoginStatus(), true);
        editor.commit();
    }


    /**
     * Stores a user session. When they check into a restaurant. This is in case they close the app
     * while they are still showing on the remote system.
     *
     * @param ID             The unique id that identifies the restaurant that the user is checking into.
     * @param restaurantName The restaurant that the user has checked in to.
     * @param tableNumber    The table number that the user is on.
     * @param phoneNumber    The phone number of the restaurant, so that the database doesn't have to be
     *                       queried again.
     *
     */
    public void createNewUserSession(int ID, String restaurantName, int tableNumber, String phoneNumber,
                                     String openingTime, String closingTime, String address) {

        //Session details
        editor.putInt(StaticVariables.getPrefRestaurantId(), ID);
        editor.putString(StaticVariables.getPrefRestaurantName(), restaurantName);
        editor.putInt(StaticVariables.getPrefTableNumber(), tableNumber);

        //Restaurant details
        editor.putString(StaticVariables.getPrefPhoneno(), phoneNumber);
        editor.putString(StaticVariables.getPrefOpentime(), openingTime);
        editor.putString(StaticVariables.getPrefClosetime(), closingTime);
        editor.putString(StaticVariables.getPrefAddress(), address);

        editor.putBoolean(StaticVariables.getPrefCheckinStatus(), true);
        editor.commit();
    }


    /**
     *
     * Sets a boolean to true indicating that the customer has placed an order that is waiting to
     * be fulfilled.
     */
    public void setOrderPlaced(boolean orderPlaced){
        editor.putBoolean(StaticVariables.getPrefOrderStatus(), orderPlaced);
        editor.commit();
    }


    /**
     *
     * @return A boolean representing whether the user has placed an order
     */
    public boolean orderIsPlaced(){
        return this.sharedPreferences.getBoolean(StaticVariables.getPrefOrderStatus(), false);
    }


    /**
     * As shared preferences isn't able to take doubles, and casting to a
     * float could cause major problems. Parsing the double to a string and saving that.
     * Not the most elegant solution to the situation, but probably the easiest. And the double
     * values shouldn't be too large.
     *
     * @param orderTotal
     */
    public void setOrderTotal(double orderTotal){
        String total = String.valueOf(orderTotal);
        editor.putString(StaticVariables.getPrefOrderTotal(), total);
        editor.commit();
    }

    /**
     * Gets the double that is stored as a string for easy and more efficient use in textViews.
     *
     * @return
     */
    public String getOrderTotalString(){
        return sharedPreferences.getString(StaticVariables.getPrefOrderTotal(), "");
    }


    public double getOrderTotal(){
        String total = sharedPreferences.getString(StaticVariables.getPrefOrderTotal(), "");
        double orderTotal = Double.parseDouble(total);

        return orderTotal;
    }


    /**
     * @return A boolean representing whether the user has checked into a restaurant or not.
     */
    public boolean isUserCheckedIn() {
        return this.sharedPreferences.getBoolean(StaticVariables.getPrefCheckinStatus(), false);
    }


    /**
     * @return A boolean. True if logged in, false if not.
     * Represents if the user is logged into the app or not
     */
    public boolean isUserLoggedIn() {
        return this.sharedPreferences.getBoolean(StaticVariables.getPrefLoginStatus(), false);
    }


    public void checkOutUser() {
        editor.remove(StaticVariables.getPrefCheckinStatus());
        editor.remove(StaticVariables.getPrefTableNumber());
        editor.remove(StaticVariables.getPrefRestaurantId());
        editor.remove(StaticVariables.getPrefRestaurantName());

        editor.remove(StaticVariables.getPrefPhoneno());
        editor.remove(StaticVariables.getPrefOpentime());
        editor.remove(StaticVariables.getPrefClosetime());

        editor.commit();
    }

    public void logOutUser() {
        editor.clear();
        editor.commit();
    }



    /*
     *
     * Getters for the Restaurant details----
     *
     */
    public String getUserName() {
        return this.sharedPreferences.getString(StaticVariables.getPrefUserName(), null);
    }

    public String getRestaurantName(){
        return this.sharedPreferences.getString(StaticVariables.getPrefRestaurantName(), null);
    }

    public String getOpenTime(){
        return this.sharedPreferences.getString(StaticVariables.getPrefOpentime(), null);
    }

    public String getCloseTime(){
        return this.sharedPreferences.getString(StaticVariables.getPrefClosetime(), null);
    }

    public String getPhoneNo(){
        return this.sharedPreferences.getString(StaticVariables.getPrefPhoneno(), null);
    }

    public String getAddress(){
        return this.sharedPreferences.getString(StaticVariables.getPrefAddress(), null);
    }

    public int getTableNum() {
        return this.sharedPreferences.getInt(StaticVariables.getPrefTableNumber(), 0);
    }


    /*
     *
     * System preferences:
     *
     */
    public void setServerIp(String ipAddress) {
        editor.putString(StaticVariables.getPrefIp(),"http://"+ipAddress);
        editor.commit();
    }

    public String getServerIp(){
        return this.sharedPreferences.getString(StaticVariables.getPrefIp(), "http://192.168.0.24");
    }

}
