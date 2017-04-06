package com.advent.sys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    public static String TAG = SplashScreenActivity.class.getSimpleName();

    public static final String MyPREFERENCES = "MyPrefsAdventSys";


    SharedPreferences sharedpreferences;
    private boolean mUserStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        final Animation animation_1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation animation_2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.anti_rotate);
        final Animation animation_3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);


        //Check if installed first time
        checkIfFirstTimeInstalled();


        imageView.startAnimation(animation_2);
        animation_2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(animation_1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation_1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(animation_3);
                finish();
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //Checking if installed first time
    //Initialize db
    private void checkIfFirstTimeInstalled() {
        if (!AppUtils.getBoolean(this, Constants.SP_FIRST_INSTALL)) {
            Log.e(TAG, "mUserACtive Set to false " + AppUtils.getBoolean(this, Constants.SP_FIRST_INSTALL));
            initializeTripId();
        }
    }

    //Create DB for first time
    private void initializeTripId() {
        DataBaseHandler dataBaseHandler = new DataBaseHandler(this);
        dataBaseHandler.insertIntoTripId();
        AppUtils.saveBoolean(this, Constants.SP_FIRST_INSTALL, true);
        Log.e(TAG, "mUserACtive updatw " + AppUtils.getBoolean(this, Constants.SP_FIRST_INSTALL));
        Log.e(TAG, "  Trip Id   Table is Created and returned to splash activity ");
    }
}