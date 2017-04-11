package com.advent.sys;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gowtham on 04/10/17.
 */

public class DateFormatter {

    public static String TAG = DateFormatter.class.getSimpleName();


    public static String startDateAndEndDateFormattedToSqlFormat(String startDateAndEndDateToBeFormatted) {

        // String mytime="24-01-2017 10:00 PM";
        Log.e(TAG," input Date of start or end date  " +startDateAndEndDateToBeFormatted);
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        Date myDate = null;
        try {
            myDate = inputFormat.parse(startDateAndEndDateToBeFormatted);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String outputDate = outputFormat.format(myDate);

        Log.e(TAG," output Date of start or end date  " +outputDate);

        return outputDate;
    }


    public static String tripDateFormattedToSqlFormat(String tripDateToBeFormatted) {

        //String mytime = "24-01-2017";
        // System.out.println("input   : " +mytime);

        Log.e(TAG, " input date of trip date to be formatted " + tripDateToBeFormatted);
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date myDate = null;
        try {
            myDate = inputFormat.parse(tripDateToBeFormatted);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String outputDate = outputFormat.format(myDate);

        // System.out.println("output   : " +finalDate);
        Log.e(TAG," output Date of trip date  " +outputDate);
        return outputDate;
    }

}
