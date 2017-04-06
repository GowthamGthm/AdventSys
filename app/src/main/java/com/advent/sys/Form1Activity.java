package com.advent.sys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.williamww.silkysignature.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Form1Activity extends AppCompatActivity implements View.OnClickListener, SignaturePad.OnSignedListener {


    TextView txt_trip_id;
    TextView txt_date;
    EditText et_start_km;
    EditText et_end_km;
    TextView txt_start_date;
    TextView txt_end_date;
    EditText et_user_name;
    EditText et_remarks;
    Button btn_submit;
    SignaturePad signaturePad;


    Button mClearButton;
    Button mSaveButton;
    Button mCompressButton;

    public static String TAG = Form1Activity.class.getSimpleName();
    Date startDate;
    Date endDate;

    int startKm;
    int endKm;
    String userName;
    String remarks;


    // for double picker date and time together
    DoubleDateAndTimePickerDialog.Builder doubleBuilder;

    SimpleDateFormat simpleDateFormat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form1);
        initializeViews();

    }


    private void initializeViews() {

        txt_trip_id = (TextView) findViewById(R.id.txtTripId);

        txt_date = (TextView) findViewById(R.id.txt_date);
        et_start_km = (EditText) findViewById(R.id.et_start_km);
        et_end_km = (EditText) findViewById(R.id.et_end_km);
        txt_start_date = (TextView) findViewById(R.id.txt_start_date_time);
        txt_end_date = (TextView) findViewById(R.id.txt_end_date_time);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_remarks = (EditText) findViewById(R.id.et_remarks);
        // img_signature = (ImageView) findViewById(R.id.img_signature);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        signaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mClearButton = (Button) findViewById(R.id.mClearButton);
        mSaveButton = (Button) findViewById(R.id.mSaveButton);
        mCompressButton = (Button) findViewById(R.id.mCompressButton);

        btn_submit.setOnClickListener(this);
        txt_start_date.setOnClickListener(this);
        signaturePad.setOnSignedListener(this);
        mClearButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mCompressButton.setOnClickListener(this);


        //txt_end_date.setOnClickListener(this);

        //Get values fron TRIP_ID_TABLE and show on auto select text
        setTripId();


        setDate();

    }

    private void setTripId() {
        DataBaseHandler databasehandler = new DataBaseHandler(this);
        int tripId = databasehandler.getTripId();
        Log.e(TAG, " TRIP ID RECEIVED ");
        txt_trip_id.setText(tripId + "");
    }

    private void setDate() {
        ///// to set the date

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        txt_date.setText(formattedDate + "");

    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btn_submit:
                collectData();
                break;

            case R.id.txt_start_date_time:
                showDateTimePicker();
                break;

            case R.id.mSaveButton:
                saveTheSignature();
                break;

            case R.id.mClearButton:
                clearSgnaturePad();
                break;

            case R.id.mCompressButton:
                compressTheSignature();
                break;


        }


    }

    public void compressTheSignature() {
        Bitmap signatureBitmap = signaturePad.getCompressedSignatureBitmap(50);
        if (addJpgSignatureToGallery(signatureBitmap)) {
            Toast.makeText(Form1Activity.this, "50% compressed signature saved into the Gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Form1Activity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
        }


    }

    public void clearSgnaturePad() {

        signaturePad.clear();
    }

    public void saveTheSignature() {
        Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
        if (addJpgSignatureToGallery(signatureBitmap)) {
            Toast.makeText(Form1Activity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Form1Activity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
        }
        if (addSvgSignatureToGallery(signaturePad.getSignatureSvg())) {
            Toast.makeText(Form1Activity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Form1Activity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
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
        Form1Activity.this.sendBroadcast(mediaScanIntent);
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

    private void showDateTimePicker() {


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


                        txt_start_date.setText(formattedStartDate);
                        txt_end_date.setText(formattedEndDate);
                    }
                });
        doubleBuilder.display();
    }


    // method of submit button
    private void collectData() {

        int trip_id = Integer.parseInt(txt_trip_id.getText().toString());


        startKm = Integer.parseInt(et_start_km.getText().toString());
        endKm = Integer.parseInt(et_end_km.getText().toString());
        userName = et_user_name.getText().toString();
        remarks = et_remarks.getText().toString();

        Bitmap signatureBitmap = signaturePad.getSignatureBitmap();

        byte[] image = BitmapUtility.getBytes(signatureBitmap);

     //   byte[] image = getBytesOfSignature(signatureBitmap);

        DataBaseHandler db = new DataBaseHandler(this);
        Log.e(TAG, "Inserting into TripDetails ");
        db.insertIntoTripDetails(trip_id, startKm, endKm, startDate, endDate, userName, remarks, image);
        Log.e(TAG, " inserted into TripDetails");
        Toast.makeText(this, "inserted the values into trip details ", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this , Form2Activity.class);
        intent.putExtra("signature",image);
        startActivity(intent);
    }



    @Override
    public void onStartSigning() {

        Toast.makeText(Form1Activity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSigned() {
        mSaveButton.setEnabled(true);
        mClearButton.setEnabled(true);
        mCompressButton.setEnabled(true);

    }

    @Override
    public void onClear() {
        mSaveButton.setEnabled(false);
        mClearButton.setEnabled(false);
        mCompressButton.setEnabled(false);

    }


}
