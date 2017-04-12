package com.advent.sys;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.advent.sys.Model.Trip;
import com.advent.sys.Model.TripDetails;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


/**
 * Created by Gowtham on 04/06/17.
 */

public class SqlUpdaterService extends IntentService {

    public static final String TAG = SqlUpdaterService.class.getSimpleName();

    public static String KEY_TRIP_ID = "trip_id";
    public static String KEY_TRIP_DATE = "trip_date";

    public static String KEY_START_KM = "start_km ";
    public static String KEY_END_KM = "end_km";

    public static String KEY_START_DATE = "start_date";
    public static String KEY_END_DATE = "end_date";

    public static String KEY_USERNAME = "user_name";
    public static String KEY_REMARKS = "remarks";
    public static String KEY_SIGNATURE = "signature";


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


                uploadTripDataJson(
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


    private void uploadTripDataJson(final int trip_id, final String trip_date, final int startKm,
                                    final int endKm, final String startDate, final String endDate,
                                    final String userName, final String remarks, final byte[] signature) {

      // creating object and initializing  to send in volley
        Trip trip = new Trip();
        trip.setTripId(String.valueOf(trip_id));
        trip.setTripDate(DateFormatter.tripDateFormattedToSqlFormat(trip_date));
        trip.getStartKm(String.valueOf(startKm));
        trip.setEndKm(String.valueOf(endKm));
        trip.setStartDate(DateFormatter.startDateAndEndDateFormattedToSqlFormat(startDate));
        trip.setEndDate(DateFormatter.startDateAndEndDateFormattedToSqlFormat(endDate));
        trip.setUserName(userName);
        trip.setRemarks(remarks);
        trip.setSignature(BitmapUtility.getStringImage(signature));

        TripDetails tripDetails = new TripDetails();
        tripDetails.setOperation(Constants.KEY_REGISTER);
        tripDetails.setTrip(trip);

        // creating json object
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("tripDetails",tripDetails);
            Log.e(TAG, " jsonresponseObject "  +jsonObject.toString());

        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest tripDetailsRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.TRIP_DETAILS_URL,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String result = response.getString("result");

                            Log.e(TAG,response.toString());
                            VolleyLog.e(TAG, "volley response is" +response);

                            if(result.equals(Constants.KEY_SUCCESS))
                            {
                                deleteColumnFromSqLite();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG," "+e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String body;
                        //get status code here
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        if(error.networkResponse.data!=null) {
                            try {
                                body = new String(error.networkResponse.data,"UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }


                        VolleyLog.e(TAG,error.toString());
                    }
                }
        );

        int socketTimeout = 60 * 1000 ;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        tripDetailsRequest.setRetryPolicy(policy);
        SingletonForVolley.getInstance(this).addToRequestQueue(tripDetailsRequest);



    }

    private void deleteColumnFromSqLite() {

        Log.e(TAG , " will delete sqlite databse");
    }


}

