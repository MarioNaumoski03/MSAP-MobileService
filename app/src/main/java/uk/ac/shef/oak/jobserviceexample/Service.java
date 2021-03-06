/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.jobserviceexample;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import uk.ac.shef.oak.jobserviceexample.utilities.Notification;

public class Service extends android.app.Service {
    protected static final int NOTIFICATION_ID = 1337;
    public static String pingResult;
    private static String TAG = "Service";
    private static Service mCurrentService;
    private int counter = 0;
    private int count=0;
    private TimerTask timertask;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "uk.ac.shef.oak.jobserviceexample";
    private String RESPONSE_KEY = "response";
    public static String responseString = "";


    public Service() {
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        mCurrentService = this;

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        responseString = mPreferences.getString(RESPONSE_KEY,"");
    }

    public String dependString()
    {
        if(responseString == "")
        {
            return pingResult;
        }
        else{
            return responseString;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "restarting Service !!");
        counter = 0;

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        try{
            if(networkInfo != null && networkInfo.isConnected())
            {
                startTimerAsync();
            }
            else
            {
                // Toast.makeText(this,"No connection",Toast.LENGTH_SHORT).show();
                count++;
                if(count<=3)
                {
                    for(int i=0; i<count; i++)
                    {
                        saveResponse();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }


    public void saveResponse(){
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString(RESPONSE_KEY,responseString);
        preferencesEditor.apply();

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new PingTask().execute();
            Log.i(TAG, "restarting foreground");
            try {
                Notification notification = new Notification();
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "Service notification", "This is the service's notification", R.drawable.ic_sleep));
                Log.i(TAG, "restarting foreground successful");

                new PingTask().execute();
            } catch (Exception e) {
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
        new PingTask().execute();
    }


    /**
     * this is called when the process is killed by Android
     *
     * @param rootIntent
     */

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }


    /**
     * static to avoid multiple timers to be created when the service is called several times
     */
    private static Timer timer;
    private static TimerTask timerTask;
    long oldTime = 0;

//    public void startTimer() {
//        Log.i(TAG, "Starting timer");
//
//        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
//        stoptimertask();
//        timer = new Timer();
//
//        //initialize the TimerTask's job
//        initializeTimerTask();
//
//        Log.i(TAG, "Scheduling...");
//        //schedule the timer, to wake up every 1 second
//        timer.schedule(timerTask, 1000, 1000); //
//    }

    public void startTimerAsync(){
        Log.i(TAG, "Starting timerAsync");
        stoptimertask();
        Timer timer = new Timer();
        initializeTimerTask();
        Log.i(TAG, "Scheduling...");
        timer.schedule(timerTask,1000,1000);
    }
    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        new PingTask().execute();
        Log.i(TAG, "initializing TimerTask");
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
        timerTask =new TimerTask(){

            @Override
            public void run() {
                new PingTask().execute();
                new PostRequestAsync().execute(dependString());
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }



    public static Service getmCurrentService() {
        return mCurrentService;
    }

    public static void setmCurrentService(Service mCurrentService) {
        Service.mCurrentService = mCurrentService;
    }


}
