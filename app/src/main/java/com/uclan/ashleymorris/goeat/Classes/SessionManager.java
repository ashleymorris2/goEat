package com.uclan.ashleymorris.goeat.Classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.uclan.ashleymorris.goeat.Activities.StartActivity;

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
    public void createNewUserSession(String username){
        editor.putString(StaticVariables.getPrefLoginName(), username);
        editor.putBoolean(StaticVariables.getPrefLoginStatus(), true);
        editor.commit();
    }


    /**
     * @return A boolean. True if logged in, false if not.
     */
    public boolean isUserLoggedIn(){
        return this.sharedPreferences.getBoolean(StaticVariables.getPrefLoginStatus(), false);
    }


    public void logOutUser(){
        editor.clear();
        editor.commit();
    }
}
