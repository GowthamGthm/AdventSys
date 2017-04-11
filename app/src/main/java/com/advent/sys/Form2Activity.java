package com.advent.sys;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.williamww.silkysignature.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Form2Activity extends AppCompatActivity implements View.OnClickListener, SignaturePad.OnSignedListener {


    SignaturePad signaturePad;
    Button mClearButton;
    Button mOkButton;

    static String NOT_SIGNED_VALUE = "no"  ;
    static String KEY_NOT_SIGNED = "key_not_signed";
    static  String KEY_SIGNATURE_BYTE = "signature_byte";
    // Button mCompressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form2);

        initializeViews();
        setListeners();


    }

    private void initializeViews() {
        signaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mClearButton = (Button) findViewById(R.id.mClearButton);
        mOkButton = (Button) findViewById(R.id.mOkButton);
        mOkButton.setEnabled(false);
        mClearButton.setEnabled(false);

    }

    private void setListeners() {

        signaturePad.setOnSignedListener(this);
        mClearButton.setOnClickListener(this);
        mOkButton.setOnClickListener(this);
        //  mCompressButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

/*

            case R.id.mSaveButton:
                saveTheSignature();
                break;
*/

            case R.id.mClearButton:
                clearSgnaturePad();
                break;

          /*  case R.id.mCompressButton:
                compressTheSignature();
                break;*/


            case R.id.mOkButton:
                sendTheSign();
        }
    }

    // method of ok button
    private void sendTheSign() {
        Bitmap signatureBitmap = signaturePad.getSignatureBitmap();

        Intent intent = new Intent();
        byte[] signImage = BitmapUtility.getBytes(signatureBitmap);
        intent.putExtra(KEY_SIGNATURE_BYTE, signImage);
        intent.putExtra(KEY_NOT_SIGNED,NOT_SIGNED_VALUE);
        setResult(2, intent);
        //finishing activity
        finish();
    }

    // method of clear button
    public void clearSgnaturePad() {

        signaturePad.clear();
    }

    // method of signature pad on signing  button views
    @Override
    public void onStartSigning() {
        mClearButton.setEnabled(false);
        mOkButton.setEnabled(false);
        Toast.makeText(Form2Activity.this, "On Start Signing", Toast.LENGTH_SHORT).show();

    }

    // method of signature pad after signing  button views
    @Override
    public void onSigned() {
        // mSaveButton.setEnabled(true);
        Toast.makeText(Form2Activity.this, "On Signed", Toast.LENGTH_SHORT).show();
        mClearButton.setEnabled(true);
        mOkButton.setEnabled(true);
        //  mCompressButton.setEnabled(true);

    }

    // method of signature pad button views
    @Override
    public void onClear() {
        //   mSaveButton.setEnabled(false);
        mClearButton.setEnabled(false);
        mOkButton.setEnabled(false);
        //  mCompressButton.setEnabled(false);

    }


    /// method for back pressed handling
    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        backButtonHandler();
    }

    private void backButtonHandler() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to Skip Signing")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        NOT_SIGNED_VALUE = "yes";
                        Intent intent = new Intent();
                        intent.putExtra(KEY_NOT_SIGNED,NOT_SIGNED_VALUE);
                        setResult(2, intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert Dialog");
        alert.show();
    }
}


    /* public void compressTheSignature() {
        Bitmap signatureBitmap = signaturePad.getCompressedSignatureBitmap(50);
        if (addJpgSignatureToGallery(signatureBitmap)) {
            Toast.makeText(Form2Activity.this, "50% compressed signature saved into the Gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Form2Activity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
        }


    }




    public void saveTheSignature() {
        Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
        if (addJpgSignatureToGallery(signatureBitmap)) {
            Toast.makeText(Form2Activity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Form2Activity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
        }
        if (addSvgSignatureToGallery(signaturePad.getSignatureSvg())) {
            Toast.makeText(Form2Activity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Form2Activity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        Form2Activity.this.sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }
*/



