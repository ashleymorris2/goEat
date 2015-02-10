package com.uclan.ashleymorris.goeat.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Ashley Morris on 08/02/2015.
 * <p/>
 * Creats an alarm that will fire off an intent every 2 minutes once a user has checked in to a
 * restaurant.
 * <p/>
 * This intent will be picked up by a broadcast receiver which will then poll the status of the customers
 * table on the server.
 */
public class RecurringAlarmSetup {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    //10 seconds, change for 1minute
    private static final long POLLING_INTERVAL = 10000;

    /**
     * Creates a pending intent that will be fired 2minutes into the future, this pending intent will
     then be picked up by the broadcast receiver to manage.

     * @param context The calling activities context.
     */
    public void createRecurringAlarm(Context context) {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RecurringPollServerService.class);

        pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        //At the current time plus 2 minutes the alarm maanager will fire an intent at
        // recurringPollReciever class. The rest of the code will be handled there.

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + POLLING_INTERVAL,
                    pendingIntent);
        } catch (NoSuchMethodError e) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + POLLING_INTERVAL,
                    pendingIntent);
        }
    }

    /**
     * Cancels a future pending intent.
     *
     * @param context The calling activities context.
     */
        public void cancelRecurringAlarm(Context context) {
            try{
                Intent intent = new Intent(context, RecurringPollServerService.class);

                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.cancel(pendingIntent);
            }
            catch(Exception ex){
                Log.e("Cancel alarm method error, in RecurringAlarmSetup.class: ", ex.toString());
            }


        }


    }



