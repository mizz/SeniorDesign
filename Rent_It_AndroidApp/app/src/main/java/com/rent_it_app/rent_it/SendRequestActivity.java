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
import android.view.Menu;
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
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.dropin.utils.PaymentMethodType;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.rent_it_app.rent_it.firebase.Config;
import com.rent_it_app.rent_it.json_models.BrainTreeEndpoint;
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

import okhttp3.ResponseBody;
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
    private TextView numDays,rate,estimateTotal,fee,taxAmount, txtPaymentMethod;
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
    private String clientToken;
    Retrofit retrofit;
    BrainTreeEndpoint braintreeEndpoint;
    private PaymentMethodNonce recentPaymentMethod;
    private static final int REQUEST_CODE = Menu.FIRST;
    public static FirebaseUser myUser;

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
        txtPaymentMethod = (TextView)findViewById(R.id.paymentMethod);
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
                                estimateTotal.setText("$ "+roundTwoDecimals(total));
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
        /*Button imageButton = (Button) findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.preview);*/

        myUser = FirebaseAuth.getInstance().getCurrentUser();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .build();

        braintreeEndpoint = retrofit.create(BrainTreeEndpoint.class);

        Call<ResponseBody> call = braintreeEndpoint.getToken(myUser.getUid());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,Response<ResponseBody> response) {
                int statusCode = response.code();
                //List<Item> items = response.body();
                try{
                    clientToken = response.body().string();
                    Log.d("myToken ",clientToken);
                    //Log.d("Client Token:", this.clientToken);
                    Log.d("Client Token:", ""+clientToken);

                    DropInResult.fetchDropInResult(SendRequestActivity.this, clientToken, new DropInResult.DropInResultListener() {
                        @Override
                        public void onError(Exception exception) {
                            // an error occurred
                        }

                        @Override
                        public void onResult(DropInResult result) {
                            if (result.getPaymentMethodType() != null) {
                                // use the icon and name to show in your UI
                                int icon = result.getPaymentMethodType().getDrawable();
                                int name = result.getPaymentMethodType().getLocalizedName();

                                if (result.getPaymentMethodType() == PaymentMethodType.ANDROID_PAY) {

                                    // The last payment method the user used was Android Pay.
                                    // The Android Pay flow will need to be performed by the
                                    // user again at the time of checkout.
                                } else {
                                    // Use the payment method show in your UI and charge the user
                                    // at the time of checkout.
                                    recentPaymentMethod = result.getPaymentMethodNonce();

                                    Log.d("paymentMethodNonce: ", recentPaymentMethod.getDescription());
                                }
                            } else {
                                // there was no existing payment method
                                Log.d("braintree.paymentMethod", ".noExistingMethod");
                            }
                        }
                    });

                    Log.d("retrofit.call.enqueue", "success");
                }catch(IOException ioe){

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call,Throwable t) {
                Log.d("retrofit.call.enqueue", "failed");
            }
        });


        Button brainTreeButton = (Button) findViewById(R.id.braintree_button);
        brainTreeButton.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view)
            {
                onBraintreeSubmit(view);
            }

        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                Log.d("paymentMethodNonce: ", result.getPaymentMethodNonce().toString());

                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("resultCode: ", "RESULT_CANCELLED");
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("Braintree.DropInUI", "onActivityResult.exception");
                Log.d("Braintree.DropInUI", "onActivityResult.exception:"+error.getMessage());
                error.printStackTrace();
            }
        }
    }

    public void onBraintreeSubmit(View v) {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(clientToken);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

}
