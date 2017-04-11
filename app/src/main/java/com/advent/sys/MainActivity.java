package com.advent.sys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button button1;
    Button button2;
    String TAG = MainActivity.class.getSimpleName();
    ConstraintLayout main_activity_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        setListeners();
        permissionCheck();
        checkNetwork();


    }

    // method to show SnackBar to User
    public void checkNetwork() {
        if (!(isNetworkAvailable() && isOnline())) {
             Snackbar.make(main_activity_layout, "Internet Connection UnAvailable ", Snackbar.LENGTH_LONG).show();


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


    // we use this to check and request the permission  using ask library
    private void permissionCheck() {

        Ask.on(this)
                .forPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withRationales("We need this permission for the signature functionality to work ")
                //  , "In order to save file you will need to grant storage permission") //optional
                .go();

    }


    @AskGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void storageAccessGranted() {
        Toast.makeText(this, " Permission Received ", Toast.LENGTH_SHORT).show();
        Log.e(TAG, " write access permitted ");

    }

    @AskDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void storageAccessDenied() {
        Ask.on(this)
                .forPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withRationales("We need this permission for the signature functionality to work ")
                .go();
        Log.e(TAG, " write access denied ");
    }


    // we use this to set the listeners
    private void setListeners() {

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    // we use this to iniatialize the views
    private void initialize() {

        button1 = (Button) findViewById(R.id.et_trip_id);
        button2 = (Button) findViewById(R.id.button2);
        main_activity_layout = (ConstraintLayout) findViewById(R.id.main_activity_layout);


    }

    // on click listeners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_trip_id:
                Intent intent = new Intent(MainActivity.this, Form1Activity.class);
                startActivity(intent);

        }
    }


}
