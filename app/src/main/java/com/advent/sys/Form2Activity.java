package com.advent.sys;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Form2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form2);

        byte[] s = getIntent().getByteArrayExtra("signature");
       Bitmap img= BitmapUtility.getImage(s);
        ImageView sigView = (ImageView) findViewById(R.id.img_sig_view);
sigView.setImageBitmap(img);
    }
}
