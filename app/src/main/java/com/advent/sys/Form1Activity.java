package com.advent.sys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Form1Activity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {


    EditText et_trip_id;
    EditText et_trip_date;
    EditText et_start_km;
    EditText et_end_km;
    EditText et_start_date_time;
    EditText et_end_date_time;
    EditText et_user_name;
    EditText et_remarks;
    Button btn_submit;

    ImageView img_signature;

    LinearLayout form1LinearLayout;


    public static String TAG = Form1Activity.class.getSimpleName();
    Date startDate;
    Date endDate;
    int tripId;
    int startKm;
    int endKm;
    String userName;
    String remarks;

    byte[] signImageByte;


    // for double picker date and time together
    DoubleDateAndTimePickerDialog.Builder doubleBuilder;

    SimpleDateFormat simpleDateFormat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form1);
        initializeViews();
        scrollViewSetUp();


    }

    // we use this method to scroll the scrollView to up , as it used to show the view from bottom
    private void scrollViewSetUp() {
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.form1NestedScrollView);
        nestedScrollView.smoothScrollTo(0, 0);


    }


    private void initializeViews() {

        et_trip_id = (EditText) findViewById(R.id.et_trip_id);
        form1LinearLayout = (LinearLayout) findViewById(R.id.form1LinearLayout);

        et_trip_date = (EditText) findViewById(R.id.et_trip_date);
        et_start_km = (EditText) findViewById(R.id.et_start_km);
        et_end_km = (EditText) findViewById(R.id.et_end_km);
        et_start_date_time = (EditText) findViewById(R.id.et_start_date_time);
        et_end_date_time = (EditText) findViewById(R.id.et_end_date_time);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_remarks = (EditText) findViewById(R.id.et_remarks);
        img_signature = (ImageView) findViewById(R.id.img_signature);
        btn_submit = (Button) findViewById(R.id.btn_submit);


        btn_submit.setOnClickListener(this);
        et_start_date_time.setOnFocusChangeListener((View.OnFocusChangeListener) this);
        img_signature.setOnClickListener(this);


        //txt_end_date.setOnClickListener(this);

        //Get values fron TRIP_ID_TABLE and show on auto select text
        setTripId();


        setDate();

    }

    private void setTripId() {
        DataBaseHandler databasehandler = new DataBaseHandler(this);
        int tripId = databasehandler.getTripId();
        Log.e(TAG, " TRIP ID RECEIVED ");
        et_trip_id.setText(tripId + "");
    }

    private void setDate() {
        ///// to set the date

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        et_trip_date.setText(formattedDate + "");

    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btn_submit:
                collectDataAndInsertToSqlite();
                break;

            /*case R.id.et_start_date_time:
                Log.e(TAG, " in switch case of onclick of start date ");
                showDateTimePicker();
                break;*/

            case R.id.img_signature:
                obtainSignResult();
                break;
        }


    }


    /// sending to new activity to get sign
    private void obtainSignResult() {
        Intent intent = new Intent(Form1Activity.this, Form2Activity.class);
        // Activity is started with requestCode 2
        startActivityForResult(intent, 2);

    }

    /// result of the sign activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            String result = data.getStringExtra(Form2Activity.KEY_NOT_SIGNED);
            Log.e(TAG," " +result);

            if (result.equals(Constants.KEY_YES)) {

                Snackbar snackbar = Snackbar
                        .make(form1LinearLayout, "Not Signed ", Snackbar.LENGTH_LONG);

                snackbar.show();
            } else {

                signImageByte = data.getByteArrayExtra(Form2Activity.KEY_SIGNATURE_BYTE);
                Log.e(TAG," in activity result else part ");
                img_signature.setImageBitmap(BitmapUtility.getImage(signImageByte));

            }

        }

    }




    private void showDateTimePicker() {

        Log.e(TAG, " in method  of onclick of start date ");
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.YEAR, 2017);
        // final Date minDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, 5);
        // final Date maxDate = calendar.getTime();

        doubleBuilder = new DoubleDateAndTimePickerDialog.Builder(this)
                //.bottomSheet()
                //.curved()

                .backgroundColor(Color.BLACK)
                .mainColor(Color.LTGRAY)
                .minutesStep(5)
                .mustBeOnFuture()

                // .minDateRange(minDate)
                // .maxDateRange(maxDate)

                .title("Double")
                .tab0Text("Start")
                .tab1Text("End")
                .listener(new DoubleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(List<Date> dates) {

                        // getting the dates from list
                        startDate = (dates.get(0));
                        endDate = (dates.get(1));

                        // formatting the date to be set as per ouur needs
                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                        String formattedStartDate = df.format(startDate);
                        String formattedEndDate = df.format(endDate);


                        et_start_date_time.setText(formattedStartDate);
                        et_end_date_time.setText(formattedEndDate);
                    }
                });
        doubleBuilder.display();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_start_date_time.getWindowToken(), 0);

        showDateTimePicker();
    }


    // method of submit button
    private void collectDataAndInsertToSqlite() {

        tripId = Integer.parseInt(et_trip_id.getText().toString());


        startKm = Integer.parseInt(et_start_km.getText().toString());
        endKm = Integer.parseInt(et_end_km.getText().toString());
        userName = et_user_name.getText().toString();
        remarks = et_remarks.getText().toString();

        //  Bitmap signatureBitmap = signaturePad.getSignatureBitmap();

        //    byte[] image = BitmapUtility.getBytes(signatureBitmap);

        //   byte[] image = getBytesOfSignature(signatureBitmap);

        DataBaseHandler db = new DataBaseHandler(this);
        Log.e(TAG, "Inserting into TripDetails ");
        db.insertIntoTripDetails(tripId, startKm, endKm, startDate, endDate, userName, remarks, signImageByte);
        Log.e(TAG, " inserted into TripDetails");
        Toast.makeText(this, "inserted the values into trip details ", Toast.LENGTH_LONG).show();

         checkNetConnectivity();
    }

    private void checkNetConnectivity() {


        if((isNetworkAvailable()) && (isOnline()))
        {
            Log.e(TAG , " Net Connectivity Available ");
            Intent newIntent = new Intent(this, SqlUpdaterService.class);
            this.startService(newIntent);

        }

    }


    // method to check if network is available
    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // method to check if device is online
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }


}
