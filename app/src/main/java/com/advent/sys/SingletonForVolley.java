package com.advent.sys;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class SingletonForVolley {

    private static SingletonForVolley mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    public static final String TAG = SingletonForVolley.class.getSimpleName();

    private SingletonForVolley(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized SingletonForVolley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonForVolley(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            Log.e(TAG, " inside the get  request q ");
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
        Log.e(TAG, " inside the add  request q ");
    }

}
