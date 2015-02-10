package com.uclan.ashleymorris.goeat.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;

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
public class RecurringPollServerService extends Service {

    private RecurringAlarmSetup recurringAlarmSetup;
    private SessionManager sessionManager;


    private static final String STATUS_URL = "/restaurant-service/scripts/get-status-script.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CODE = "status_code";
    private static final String TAG_MESSAGE = "message";



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        recurringAlarmSetup = new RecurringAlarmSetup();
        sessionManager = new SessionManager(this);


        PollServerTask pollServerTask = new PollServerTask();
        pollServerTask.execute();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class PollServerTask extends AsyncTask<Void, Void, JSONObject> {

        private JSONParser jsonParser = new JSONParser();
        @Override
        protected JSONObject doInBackground(Void... voids) {

            int tableNumber = sessionManager.getTableNum();
            String url = sessionManager.getServerIp()+STATUS_URL;

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("table_number", String.valueOf(tableNumber)));

            //Make a new request to the server
            //Run on a seperate thread.
            return jsonParser.makeHttpRequest(url, HttpPost.METHOD_NAME, params);
        }

        @Override
        protected void onPostExecute(JSONObject jsonResponse) {
            super.onPostExecute(jsonResponse);

            if(jsonResponse != null){
                try {
                    int successCode = jsonResponse.getInt(TAG_SUCCESS);

                    if(successCode == 1){
                        int statusCode = jsonResponse.getInt(TAG_CODE);

                        //If the status codes don't match then the phone hasn't been updated
                        if(sessionManager.getSessionStatus() != statusCode){

                            String message = jsonResponse.getString(TAG_MESSAGE);

                            //Update the phone to the server status code
                            sessionManager.setSessionStatus(statusCode);

                            //Build and then show a notification
                            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationManager notificationManager = (NotificationManager)
                                    getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

                            Notification notification = new Notification.Builder(getBaseContext())
                                    .setContentTitle("Go Eat")
                                    .setContentText(message)
                                    .setSmallIcon(R.drawable.ic_stat_ic_action_burger8)
                                    .setVibrate(new long[]{1000, 500, 1500})
                                    .setTicker(message)
                                    .setSound(sound)
                                    .build();

                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notificationManager.notify(1, notification);

                            if(statusCode == 5){
                                //The order is now complete, can clear it from the system.
                                sessionManager.setOrderPlaced(false);
                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //Make future alarm unless the status code is 0
            if(sessionManager.getSessionStatus() != 0) {
                recurringAlarmSetup.createRecurringAlarm(getBaseContext());
            }

        }
    }
}
