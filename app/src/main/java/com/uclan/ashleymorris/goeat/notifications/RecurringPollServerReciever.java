package com.uclan.ashleymorris.goeat.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.uclan.ashleymorris.goeat.R;
import com.uclan.ashleymorris.goeat.classes.JSONParser;
import com.uclan.ashleymorris.goeat.classes.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashley Morris on 08/02/2015.
 */
public class RecurringPollServerReciever extends BroadcastReceiver {

    private RecurringAlarmSetup recurringAlarmSetup;
    private SessionManager sessionManager;
    private JSONParser jsonParser;

    private static final String STATUS_URL = "/restaurant-service/scripts/get-status-script.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CODE = "code";
    private static final String TAG_MESSAGE = "message";

    @Override
    public void onReceive(Context context, Intent intent) {

        recurringAlarmSetup = new RecurringAlarmSetup();
        sessionManager = new SessionManager(context);
        jsonParser = new JSONParser();

        int tableNumber = sessionManager.getTableNum();
        String url = sessionManager.getServerIp()+STATUS_URL;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("table_number", String.valueOf(tableNumber)));

        //Make a new request to the server
        //Run on a seperate thread.
        JSONObject jsonResponse = jsonParser.makeHttpRequest(url, HttpPost.METHOD_NAME, params);

        if(jsonResponse != null){
            try {
                int successCode = jsonResponse.getInt(TAG_SUCCESS);

                if(successCode == 1){
                    int statusCode = jsonResponse.getInt(TAG_CODE);

                    //If the status codes don't match then the phone hasn't been updated
                    if(sessionManager.getTableStatus() != statusCode){

                        String message = jsonResponse.getString(TAG_MESSAGE);

                        //Update the phone to the server status code
                        sessionManager.setTableStatus(statusCode);

                        //Build and then show a notification
                        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationManager notificationManager = (NotificationManager)
                                context.getSystemService(Context.NOTIFICATION_SERVICE);

                        Notification notification = new Notification.Builder(context)
                                .setContentTitle("Go Eat")
                                .setContentText(message)
                                .setSmallIcon(R.drawable.ic_stat_ic_action_burger8)
                                .setVibrate(new long[]{1000, 500, 1500})
                                .setSound(sound)
                                .build();

                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        notificationManager.notify(1, notification);

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Make future alarm unless the status code is 0
        if(sessionManager.getTableStatus() != 0) {
            recurringAlarmSetup.createRecurringAlarm(context);
        }
    }
}
