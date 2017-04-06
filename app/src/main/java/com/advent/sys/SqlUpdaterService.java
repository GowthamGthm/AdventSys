package com.advent.sys;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Hashtable;
import java.util.Map;


/**
 * Created by Gowtham on 04/06/17.
 */

public class SqlUpdaterService extends IntentService {

    public static final String TAG = SqlUpdaterService.class.getSimpleName();

    public static String KEY_TRIP_ID;
    public static String KEY_TRIP_DATE;

    public static String KEY_START_KM;
    public static String KEY_END_KM;

    public static String KEY_START_DATE;
    public static String KEY_END_DATE;

    public static String KEY_USERNAME;
    public static String KEY_REMARKS;
    public static String KEY_SIGNATURE;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public SqlUpdaterService(String name) {

        super(name);
        Log.e(TAG, "entered service default ");
    }


    public SqlUpdaterService() {
        // Used to name the worker thread, important only for debugging.
        super("SqlUpdaterService");
        Log.e(TAG, "entered service empty constructor ");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        getDataToUpload();

    }

    private void getDataToUpload() {
        DataBaseHandler dbHandler = new DataBaseHandler(this);

        Cursor cursor = dbHandler.getUnsyncedData();
        if (cursor.moveToFirst()) {
            do {
                //calling the method to save the unsynced name to MySQL

                Log.e(TAG, "  " + cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_TRIP_ID)));
                Log.e(TAG, "  " + cursor.getString(cursor.getColumnIndex(dbHandler.KEY_TRIP_DATE)));
                Log.e(TAG, "  " + cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_START_KM)));
                Log.e(TAG, "  " + cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_END_KM)));
                Log.e(TAG, "  " + cursor.getString(cursor.getColumnIndex(dbHandler.KEY_START_DATE)));
                Log.e(TAG, "  " + cursor.getString(cursor.getColumnIndex(dbHandler.KEY_END_DATE)));
                Log.e(TAG, "  " + cursor.getString(cursor.getColumnIndex(dbHandler.KEY_USERNAME)));
                Log.e(TAG, "  " + cursor.getString(cursor.getColumnIndex(dbHandler.KEY_REMARKS)));
             //   Log.e(TAG, "  " + cursor.getBlob(cursor.getColumnIndex(dbHandler.KEY_SIGNATURE)));


                uploadTripData(
                        cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_TRIP_ID)),
                        cursor.getString(cursor.getColumnIndex(dbHandler.KEY_TRIP_DATE)),

                        cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_START_KM)),
                        cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_END_KM)),

                        cursor.getString(cursor.getColumnIndex(dbHandler.KEY_START_DATE)),
                        cursor.getString(cursor.getColumnIndex(dbHandler.KEY_END_DATE)),

                        cursor.getString(cursor.getColumnIndex(dbHandler.KEY_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(dbHandler.KEY_REMARKS)),
                        cursor.getBlob(cursor.getColumnIndex(dbHandler.KEY_SIGNATURE))


                );
            } while (cursor.moveToNext());
        }


    }


    private void uploadTripData(final int trip_id, final String trip_date, final int startKm, final int endKm, final String startDate, final String endDate, final String userName, final String remarks, final byte[] signature) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.TRIP_DETAILS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.e(TAG, " in onresponse method");
                        //Showing toast message of the response
                        Toast.makeText(SqlUpdaterService.this, s, Toast.LENGTH_LONG).show();
                        Log.e(TAG, s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        //Showing toast
                        Toast.makeText(SqlUpdaterService.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, volleyError.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String stringSignature = BitmapUtility.getStringImage(signature);


                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                Log.e(TAG," hash Map values ");
                Log.e(TAG," " +trip_id+ "\n" +trip_date+ "\n " +startKm+ "\n " +endKm+ "\n " +startDate+ "\n " +endDate+ "\n " +userName+ "\n "+remarks);

                //Adding parameters
                params.put(KEY_TRIP_ID, Integer.toString(trip_id));
                params.put(KEY_TRIP_DATE, trip_date);

                params.put(KEY_START_KM, String.valueOf(startKm));
                params.put(KEY_END_KM, String.valueOf(endKm));

                params.put(KEY_START_DATE, startDate);
                params.put(KEY_END_DATE, endDate);

                params.put(KEY_USERNAME, userName);
                params.put(KEY_REMARKS, remarks);
                params.put(KEY_SIGNATURE, stringSignature);


                //returning parameters
                return params;
            }
        };

        Log.e(TAG, " before adding request queue");
        SingletonForVolley.getInstance(this).addToRequestQueue(stringRequest);
        Log.e(TAG, "After Adding Request Queue");

    }
}

