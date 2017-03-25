package com.rent_it_app.rent_it;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import com.rent_it_app.rent_it.firebase.Config;
import com.rent_it_app.rent_it.json_models.Item;
import com.rent_it_app.rent_it.json_models.ItemEndpoint;
import com.rent_it_app.rent_it.json_models.Rental;
import com.rent_it_app.rent_it.testing.NotificationActivity;
import com.rent_it_app.rent_it.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by malhan on 3/4/17.
 */

public class SendRequestActivity extends BaseActivity{


    Rental thisRental;
    private EditText txtDate;
    private TextView numDays,rate,estimateTotal,fee,taxAmount;
    private Button btnDatePicker, btnRequest;
    private String myIssue, myItem, myReason, myRental, mDate;
    private int mYear, mMonth, mDay, mHour, mMinute, myRole;
    private ImageView preview;
    private Long diff,days;
    private ImageView ivImage;
    private Double dailyRate,total,serviceFee,tax,sales;
    private Calendar returnday;
    private final Double TAX_RATE = 0.06;
    private final Double SERVICE_FEE_RATE = 0.05;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    CognitoCachingCredentialsProvider credentialsProvider;
    CognitoSyncManager syncClient;
    File photo_destination;
    String imgS3Name;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("START RENTAL");

        txtDate = (EditText)findViewById(R.id.in_date);
        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnRequest=(Button) findViewById(R.id.request_button);
        numDays = (TextView)findViewById(R.id.days);
        rate = (TextView)findViewById(R.id.dailyRate);
        estimateTotal =(TextView)findViewById(R.id.total);
        fee = (TextView)findViewById(R.id.searviceCharge);
        taxAmount = (TextView)findViewById(R.id.tax);
        /*dailyRate = 3.50;//temp
        rate.setText("$ "+dailyRate);*/
        thisRental = (Rental) getIntent().getSerializableExtra(Config.THIS_RENTAL);
        dailyRate = thisRental.getItem().getRate();
        rate.setText("$ "+dailyRate);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);


                DatePickerDialog datePickerDialog = new DatePickerDialog(SendRequestActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);

                                returnday = Calendar.getInstance();
                                returnday.set(Calendar.YEAR,year);
                                returnday.set(Calendar.MONTH,monthOfYear);
                                returnday.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                returnday.set(Calendar.HOUR_OF_DAY, 0);
                                returnday.set(Calendar.MINUTE, 0);
                                returnday.set(Calendar.SECOND, 0);
                                returnday.set(Calendar.MILLISECOND, 0);

                                diff = returnday.getTimeInMillis() - c.getTimeInMillis(); //result in millis
                                days = (diff / (24 * 60 * 60 * 1000))+1;
                                //days = TimeUnit.MILLISECONDS.toDays(diff);
                                //Log.d("diff"," "+diff);
                                numDays.setText(days+" days");
                                sales = dailyRate*days;
                                serviceFee = sales*SERVICE_FEE_RATE;
                                fee.setText("$ "+roundTwoDecimals(serviceFee));
                                tax = (sales+serviceFee)*TAX_RATE;
                                taxAmount.setText("$ "+roundTwoDecimals(tax));
                                total = sales+roundTwoDecimals(serviceFee)+roundTwoDecimals(tax);
                                estimateTotal.setText("$ "+total);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();



            }
        });


        btnRequest.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view)
            {
                startActivity(new Intent(SendRequestActivity.this, NotificationActivity.class));
            }

        });

        // Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                this,  // getApplicationContext(),
                Constants.COGNITO_POOL_ID, // Identity Pool ID
                Regions.US_WEST_2 // Region
        );

        // Initialize the Cognito Sync client
        syncClient = new CognitoSyncManager(
                this,
                Regions.US_WEST_2, // Region
                credentialsProvider);

        //Button - Image
        Button imageButton = (Button) findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.preview);

    }

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(SendRequestActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        photo_destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            photo_destination.createNewFile();
            fo = new FileOutputStream(photo_destination);
            fo.write(bytes.toByteArray());
            fo.close();
            Log.d("photo_des.createNewFile", "success");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.d("photo_dest!=null?", photo_destination.toString());

        //Rsizing the image
        Bitmap resized = Bitmap.createScaledBitmap(thumbnail, 900, 600, true);

        /*ivImage.setImageBitmap(thumbnail);*/
        ivImage.setImageBitmap(resized);

        //Log.d(TAG, "Height: " + resized.getHeight() + " & Width: " + resized.getWidth());
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        photo_destination = new File(getRealPathFromURI(this, getImageUri(this, bm)));

        ivImage.setImageBitmap(bm);
        Log.d("PATH",""+ photo_destination);
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Context inContext, Uri uri) {
        Cursor cursor = inContext.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
