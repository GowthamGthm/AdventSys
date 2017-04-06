package com.advent.sys;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button button1;
    Button button2;
    String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        setListeners();
        permissionCheck();


    }
    // we use this to check and request the permission  using ask library
    private void permissionCheck() {

        Ask.on(this)
                .forPermissions( Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withRationales("We need this permission for the signature functionality to work ")
                     //  , "In order to save file you will need to grant storage permission") //optional
                .go();

    }


    @AskGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void storageAccessGranted()
    {
        Toast.makeText(this," Permission Received " , Toast.LENGTH_SHORT).show();
        Log.e(TAG," write access permitted ");

    }

    @AskDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void storageAccessDenied()
    {
        Ask.on(this)
                .forPermissions( Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withRationales("We need this permission for the signature functionality to work ")
                .go();
        Log.e(TAG," write access denied ");
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
