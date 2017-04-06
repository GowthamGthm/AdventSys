package com.advent.sys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Gowtham on 04/02/17.
 */

public class InternetCheckReceiver extends BroadcastReceiver {

    private Context context;
    private DataBaseHandler db;
    public final static String TAG = InternetCheckReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
        /// do something here
        this.context = context;

        db = new DataBaseHandler(context);


        //Check if internet avail

        Log.e(TAG,"Broadcast received ");

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                Log.e(TAG,"Internet available is true , in the if case  ");

                //If true call service
                Intent newIntent = new Intent(context, SqlUpdaterService.class);
                context.startService(newIntent);

            }


        }




    } // onreceive




}// main
