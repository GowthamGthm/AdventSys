package com.advent.sys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Gowtham on 03/31/17.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    public static String TAG = DataBaseHandler.class.getSimpleName();

    // Database Name
    private static final String DATABASE_NAME = "adventsys";

    // Contacts table name
    private static final String TABLE_TRIP_ID = "tripid";
    private static final String TABLE_TRIP_DETAILS = "tripdetails";


    // columns of trip id table
    private static final String KEY_ID = "id";
    private static final String KEY_USED = "used";

    // column of trip details table
    public static final String KEY_TRIP_ID = "tripid";
    public static final String KEY_TRIP_DATE = "tripdate";
    public static final String KEY_START_KM = "startkm";
    public static final String KEY_END_KM = "endkm";
    public static final String KEY_START_DATE = "startdate";
    public static final String KEY_END_DATE = "enddate";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_REMARKS = "remarks";
    public static final String KEY_SIGNATURE = "signature";
    public static final String KEY_STATUS = "sync_status";


    // trip ID set 1 and 0
    private static final int DEFAULT = 0;
    private static final int SETONE = 1;


// create statement for trip_id

    String CREATE__TABLE_ID = " CREATE TABLE " + TABLE_TRIP_ID + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY , "
            + KEY_USED + " INTEGER DEFAULT " + DEFAULT + " ); ";

    // create statement for trip_details

   /* CREATE TABLE Orders (
            OrderID int NOT NULL,
            OrderNumber int NOT NULL,
            PersonID int,
            PRIMARY KEY (OrderID),
    FOREIGN KEY (PersonID) REFERENCES Persons(PersonID)
            );

   */


    String CREATE__TABLE_TRIP_DETAILS = " CREATE TABLE " + TABLE_TRIP_DETAILS + " ( "
            + KEY_TRIP_ID + " INTEGER NOT NULL ,"
            + KEY_TRIP_DATE + " DATE ,"
            + KEY_START_KM + " INTEGER NOT NULL , "
            + KEY_END_KM + " INTEGER NOT NULL ,"
            + KEY_START_DATE + " DATE ,"
            + KEY_END_DATE + " DATE ,"
            + KEY_USERNAME + " VARCHAR(20) ,"
            + KEY_SIGNATURE + " BLOB NOT NULL , "
            + KEY_REMARKS + " VARCHAR(300) ,"
            +KEY_STATUS + " INTEGER DEFAULT " + DEFAULT + " ); ";



    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(CREATE__TABLE_ID);
        db.execSQL(CREATE__TABLE_TRIP_DETAILS);
        Log.e(TAG, "  Trip Id Table Created ");
        Log.e(TAG, " Trip Details Table Created ");


    }


    public void insertIntoTripId() {

        //Get sql reference for writing
        SQLiteDatabase db = this.getWritableDatabase();

        // getting the database to write
        ContentValues values = new ContentValues();
        int i;
        for (i = 1; i < 100; i++) {
            values.put(KEY_ID, i); // Contact Name
            // Inserting Row
            db.insertWithOnConflict(TABLE_TRIP_ID, null, values, SQLiteDatabase.CONFLICT_ABORT);
        }
        // Closing database connection
        db.close();
        Log.e(TAG, "  Trip Id Table value inserted ");
    }

    //Get trip id
    int getTripId() {

        int id = 0;
        int used;

        String selectQuery = "SELECT  * FROM " + TABLE_TRIP_ID;

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows
        if (c.moveToFirst()) {
            Log.e(TAG, "Table has some values");
            do {
                id = (c.getInt((c.getColumnIndex(KEY_ID))));
                used = ((c.getInt(c.getColumnIndex(KEY_USED))));
                Log.e(TAG, "Table values " + id + " " + used);

                if (used == 0) {
                    updateTripIdUsed(id);
                    break;
                }


            } while (c.moveToNext());
        }

        Log.e(TAG, " trip id will be returned  ");
        return id;


    }

    private void updateTripIdUsed(int id) {

        Log.e(TAG, "Update called");
       /* int updateId;

        id= updateId;*/

        SQLiteDatabase db = this.getWritableDatabase();

        // getting the database to write
//        ContentValues contentValues = new ContentValues();
        ContentValues cv = new ContentValues();
        cv.put(KEY_USED, SETONE);

        // update statement
        db.update(TABLE_TRIP_ID, cv, KEY_ID + " = " + id, null);

       /* String upDateQuery = " UPDATE " +TABLE_TRIP_ID+ " SET " +KEY_USED+ " = " +SETONE+  " WHERE " +KEY_ID+ " = " +id+ " ; " ;*/


    }


    // method to insert trip details
    void insertIntoTripDetails(int trip_id, int start_km, int end_km, Date start_date, Date end_date, String userName, String remarks, byte[] signature) {
        Log.e(TAG, " got  into trip details method ");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TRIP_ID, trip_id);
        contentValues.put(KEY_TRIP_DATE, getDate());
        contentValues.put(KEY_START_KM, start_km);
        contentValues.put(KEY_END_KM, end_km);
        contentValues.put(KEY_START_DATE, formattedDateWithTime(start_date));
        contentValues.put(KEY_END_DATE, formattedDateWithTime(end_date));
        contentValues.put(KEY_USERNAME, userName);
        contentValues.put(KEY_REMARKS, remarks);
        contentValues.put(KEY_SIGNATURE, signature);
        db.insertWithOnConflict(TABLE_TRIP_DETAILS,null,contentValues,SQLiteDatabase.CONFLICT_ABORT);
        Log.e(TAG, " inserted the values  into TripDetails");

    }

    // method to get the date from java
    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    // the time start and end date is received as date and formatted here
    private String formattedDateWithTime(Date toBeFormatted) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        return df.format(toBeFormatted);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP_ID);
        Log.e(TAG, " Trip ID Table Dropped ");

        // Create tables again
        onCreate(db);
        Log.e(TAG, "  Trip Id Table Destroyed ");

    }


    // this method is used to get sync status
    public Cursor getUnsyncedData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_TRIP_DETAILS + " WHERE " + KEY_STATUS + " = " +DEFAULT + " ; " ;
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


}
