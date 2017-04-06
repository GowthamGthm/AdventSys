package com.advent.sys;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapUtility {


    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


    public static String getStringImage(byte[] byteArray){
       // ByteArrayOutputStream strOpStream = new ByteArrayOutputStream();
       // bmp.compress(Bitmap.CompressFormat.JPEG, 100, strOpStream);
       // byte[] imageBytes = strOpStream.toByteArray();
        String encodedStringImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedStringImage;
    }

}
