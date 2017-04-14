package com.rent_it_app.rent_it;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.dropin.utils.PaymentMethodType;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.rent_it_app.rent_it.firebase.Config;
import com.rent_it_app.rent_it.json_models.BraintreeEndpoint;
import com.rent_it_app.rent_it.json_models.Rental;
import com.rent_it_app.rent_it.json_models.RentalEndpoint;
import com.rent_it_app.rent_it.json_models.RentalPaymentMethodNonce;
import com.rent_it_app.rent_it.json_models.TransactionAmount;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by malhan on 3/4/17.
 */

public class InitiateReturnActivity extends BaseActivity{


    Rental thisRental;
    private EditText txtDate,txtNotes;
    private TextView numDays,rate,estimateTotal,fee,taxAmount, txtPaymentMethod, tvPeriod, tvReturn, rentalFee;
    private Button btnDatePicker, btnRequest;
    private String myIssue, myItem, myReason, myRental, mDate;
    private int mYear, mMonth, mDay, mHour, mMinute, myRole;
    private ImageView preview, imgPayment;
    private Long diff,days,hours,diff2;
    private ImageView ivImage;
    private Double dailyRate,total,serviceFee,tax,sales;
    private Calendar returnday,c;
    private final Double TAX_RATE = 0.06;
    private final Double SERVICE_FEE_RATE = 0.05;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private String clientToken;
    Retrofit retrofit;
    BraintreeEndpoint braintreeEndpoint;
    RentalEndpoint rentalEndpoint;
    private PaymentMethodNonce paymentMethodNonce;
    private static final int REQUEST_CODE = Menu.FIRST;
    public static FirebaseUser myUser;
    private String paymentMethodDescription;
    Gson gson;

    private Typeface ralewayRegular,aaargh,josefinsans_regular,latoLight,latoRegular;
    private TextView lblReturn,lblCharg,lblPayment,lblNotes,lblPolicy1,lblPolicy2,lblText1,lblText2;
    private TextView lblRate,lblDays,lblRental,lblService,lblTax,lblTotal,lblPeriod;

    CognitoCachingCredentialsProvider credentialsProvider;
    CognitoSyncManager syncClient;
    File photo_destination;
    String imgS3Name;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_return);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SpannableString s = new SpannableString("RETURN ITEM");
        toolbar.setNavigationIcon(R.drawable.white_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        s.setSpan(new TypefaceSpan("fonts/raleway_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.getSupportActionBar().setTitle(s);

        ralewayRegular = Typeface.createFromAsset(getAssets(),  "fonts/raleway_regular.ttf");
        aaargh = Typeface.createFromAsset(getAssets(),  "fonts/aaargh.ttf");
        josefinsans_regular = Typeface.createFromAsset(getAssets(),  "fonts/josefinsans_regular.ttf");
        latoLight = Typeface.createFromAsset(getAssets(),  "fonts/lato_light.ttf");
        latoRegular = Typeface.createFromAsset(getAssets(),  "fonts/lato_regular.ttf");


        txtDate = (EditText)findViewById(R.id.in_date);
        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnRequest=(Button) findViewById(R.id.request_button);
        //numDays = (TextView)findViewById(R.id.days);
        rate = (TextView)findViewById(R.id.dailyRate);
        estimateTotal =(TextView)findViewById(R.id.total);
        fee = (TextView)findViewById(R.id.searviceCharge);
        taxAmount = (TextView)findViewById(R.id.tax);
        txtPaymentMethod = (TextView)findViewById(R.id.paymentMethod);
        txtNotes = (EditText)findViewById(R.id.notes);
        rentalFee = (TextView)findViewById(R.id.rentalCharge);
        imgPayment =(ImageView) findViewById(R.id.img_payment);
        /*dailyRate = 3.50;//temp
        rate.setText("$ "+dailyRate);*/
        thisRental = (Rental) getIntent().getSerializableExtra(Config.THIS_RENTAL);
        dailyRate = thisRental.getItem().getRate();
        rate.setText("$ "+dailyRate);
        /*TimeZone tz = TimeZone.getTimeZone("America/New_York");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm 'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());*/
        c = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("America/New_York");
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        df.setTimeZone(tz);
        String returnDate = df.format(c.getTime());
        Log.d("NOW: ",returnDate);
        //DateTime returnDate = ISODateTimeFormat.dateTime().parseDateTime(nowAsISO);


        tvPeriod = (TextView)findViewById(R.id.rentalPeriod);
        //tvReturn = (TextView)findViewById(R.id.returnDate);
        //Log.d("start date: ",""+thisRental.getRentalStartedDate());
        DateTime startDate = ISODateTimeFormat.dateTime().parseDateTime(thisRental.getRentalStartedDate());

        //date.setText("Submitted: "+ dateTimeObj.toString( "MM/dd/yyyy"))
        //String rental_started = thisRental.getRentalStartedDate();
        Log.d("start date: ",""+startDate.toString( "MM/dd/yyyy hh:mm a"));

        //tvReturn.setText("Return Now: "+returnDate);
        diff = c.getTimeInMillis() - startDate.getMillis();
        hours = (diff / (60 * 60 * 1000));
        days = (diff / (24 * 60 * 60 * 1000));
        diff2 = hours - (24*days);
        Log.d("hours diff: ",""+ diff2);
        Log.d("hours: ",""+hours);
        //numDays.setText(days+" days "+diff2+" hours");
        tvPeriod.setText(days+" days "+diff2+" hours");
        //tvReturn.setText("Rental Started: "+startDate.toString( "MM/dd/yyyy hh:mm a"));
        sales = dailyRate*days+diff2*(dailyRate/24);
        rentalFee.setText("$ "+roundTwoDecimals(sales));
        serviceFee = sales*SERVICE_FEE_RATE;
        fee.setText("$ "+roundTwoDecimals(serviceFee));
        tax = (sales+serviceFee)*TAX_RATE;
        taxAmount.setText("$ "+roundTwoDecimals(tax));
        total = sales+roundTwoDecimals(serviceFee)+roundTwoDecimals(tax);
        estimateTotal.setText("$ "+roundTwoDecimals(total));

       /* c = Calendar.getInstance();
        Log.d("NOW 1: ",c.toString());*/
        //font
        rate.setTypeface(ralewayRegular);
        //numDays.setTypeface(ralewayRegular);
        rentalFee.setTypeface(ralewayRegular);
        fee.setTypeface(ralewayRegular);
        taxAmount.setTypeface(ralewayRegular);
        estimateTotal.setTypeface(ralewayRegular);
        txtPaymentMethod.setTypeface(latoRegular);

        lblPeriod = (TextView)findViewById(R.id.lblPeriod);
        lblPeriod.setTypeface(ralewayRegular);
        lblCharg = (TextView)findViewById(R.id.lblCharge);
        lblCharg.setTypeface(ralewayRegular);
        lblNotes = (TextView)findViewById(R.id.lblNotes);
        lblNotes.setTypeface(ralewayRegular);
        lblPayment = (TextView)findViewById(R.id.lblPayment);
        lblPayment.setTypeface(ralewayRegular);
        /*lblReturn = (TextView)findViewById(R.id.lblReturn);
        lblReturn.setTypeface(ralewayRegular);*/
        lblPolicy1 = (TextView)findViewById(R.id.lblPolicy1);
        lblPolicy1.setTypeface(latoRegular);
        lblPolicy2 = (TextView)findViewById(R.id.lblPolicy2);
        lblPolicy2.setTypeface(latoRegular);
        lblText1 = (TextView)findViewById(R.id.lblText1);
        lblText1.setTypeface(latoLight);
        lblText2 = (TextView)findViewById(R.id.lblText2);
        lblText2.setTypeface(latoLight);
        lblRate = (TextView)findViewById(R.id.lblRate);
        lblRate.setTypeface(josefinsans_regular);
        lblRental = (TextView)findViewById(R.id.lblRental);
        lblRental.setTypeface(josefinsans_regular);
        lblService = (TextView)findViewById(R.id.lblService);
        lblService.setTypeface(josefinsans_regular);
        lblTax = (TextView)findViewById(R.id.lblTax);
        lblTax.setTypeface(josefinsans_regular);
        lblTotal = (TextView)findViewById(R.id.lblTotal);
        lblTotal.setTypeface(ralewayRegular);


        gson = new Gson();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        braintreeEndpoint = retrofit.create(BraintreeEndpoint.class);
        rentalEndpoint = retrofit.create(RentalEndpoint.class);



        btnRequest.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view)
            {

                //String rental_id = thisRental.getRentalId();
                thisRental.setRentalStatus(2);// 2 means renting
                //thisRental.getBookedStartDate();
                TimeZone tz = TimeZone.getTimeZone("America/New_York");
                DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                df2.setTimeZone(tz);
                //String nowAsISO = df.format(new Date());
                String returnAsISO = df2.format(c.getTime());
                thisRental.setRentalEndDate(returnAsISO);
                //thisRental.setBookedStartDate(c.toString());
                /*thisRental.setBookedEndDate(returnAsISO);
                thisRental.setBookedPeriod(days);
                thisRental.setEstimatedProfit(roundTwoDecimals(sales));*/
                thisRental.setNotes(txtNotes.getText().toString());
                thisRental.setPaymentStatus(2);//payment paid
                //thisRental.setDailyRate(dailyRate);
                thisRental.setRentalPeriod(diff.doubleValue());
                thisRental.setRentalFee(sales);
                thisRental.setServiceFee(serviceFee);
                thisRental.setTax(tax);
                thisRental.setTotal(total);

                String rental_id = thisRental.getRentalId();

                //update Rental Table
                Call<Rental> call = rentalEndpoint.returnItem(rental_id,thisRental);
                call.enqueue(new Callback<Rental>() {
                    @Override
                    public void onResponse(Call<Rental> call, Response<Rental> response) {
                        int statusCode = response.code();

                        Log.d("retrofit.call.enqueue", "" + statusCode);

                        //Log.d("photo_dest!=null?", photo_destination.toString());
                        Intent myIntent = new Intent(InitiateReturnActivity.this, RequestSentActivity.class);
                        //myIntent.putExtra(Config.MORE_DATA, brwsList.get(pos));
                        InitiateReturnActivity.this.startActivity(myIntent);

                    }

                    @Override
                    public void onFailure(Call<Rental> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                    }

                });

                /*process payment*/
                // Create Transaction including paymentMethodNonce, total here
                /*Transaction transaction = new Transaction();
                transaction.setChargeAmount(roundTwoDecimals(total));
                transaction.setPaymentMethodNonce(paymentMethodNonce.getNonce());
                transaction.setUser(myUser.getUid());

                // Send Transaction to server via Retrofit
                Call<Transaction> call2 = braintreeEndpoint.processTransaction(transaction);
                call2.enqueue(new Callback<Transaction>() {
                    @Override
                    public void onResponse(Call<Transaction> call,Response<Transaction> response) {
                        int statusCode = response.code();

                        // success
                    }

                    @Override
                    public void onFailure(Call<Transaction> call,Throwable t) {
                        Log.d("retrofit.call.enqueue", "failed");
                    }
                });*/

                savePaymentMethodToken();

                //send notification to owner
                /*Call<ResponseBody> call = braintreeEndpoint.startRentalNotification(thisRental.getRentalId());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        int statusCode = response.code();

                        Log.d("retrofit.call.enqueue", "" + statusCode);

                        //Log.d("photo_dest!=null?", photo_destination.toString());

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                    }

                });*/



            }

        });

        /*// Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                this,  // getApplicationContext(),
                Constants.COGNITO_POOL_ID, // Identity Pool ID
                Regions.US_WEST_2 // Region
        );

        // Initialize the Cognito Sync client
        syncClient = new CognitoSyncManager(
                this,
                Regions.US_WEST_2, // Region
                credentialsProvider);*/

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

                    DropInResult.fetchDropInResult(InitiateReturnActivity.this, clientToken, new DropInResult.DropInResultListener() {
                        @Override
                        public void onError(Exception exception) {
                            // an error occurred
                        }

                        @Override
                        public void onResult(DropInResult result) {
                            if (result.getPaymentMethodType() != null) {
                                // use the icon and name to show in your UI
                                /*int icon = result.getPaymentMethodType().getDrawable();
                                int name = result.getPaymentMethodType().getLocalizedName();*/


                                Log.d("user_id: ", myUser.getUid());
                                Log.d("rental_id: ", thisRental.getRentalId());

                                paymentMethodDescription = result.getPaymentMethodType().getCanonicalName()+" "+result.getPaymentMethodNonce().getDescription();
                                txtPaymentMethod.setText(paymentMethodDescription);
                                Log.d("getPaymentMethodType()", ".getCanonicalName():"+result.getPaymentMethodType().getCanonicalName());
                                Log.d("getPaymentMethodType()", ".name():"+result.getPaymentMethodType().name());
                                Log.d("getPaymentMethodType()", ".toString():"+result.getPaymentMethodType().toString());
                                Log.d("getPaymentMethodNonce()", ".getDescription():"+result.getPaymentMethodNonce().getDescription());

                                //icon.setImageResource(result.getPaymentMethodType().getDrawable());
                                //icon.setImageResource(result.getPaymentMethodType().getVaultedDrawable());
                                imgPayment.setImageResource(result.getPaymentMethodType().getDrawable());

                                //icon.setImageResource(result.getPaymentMethodType().getDrawable());
                                //icon.setImageResource(result.getPaymentMethodType().getVaultedDrawable());

                                if (result.getPaymentMethodType() == PaymentMethodType.ANDROID_PAY) {



                                    // The last payment method the user used was Android Pay.
                                    // The Android Pay flow will need to be performed by the
                                    // user again at the time of checkout.
                                } else {
                                    // Use the payment method show in your UI and charge the user
                                    // at the time of checkout.
                                    paymentMethodNonce = result.getPaymentMethodNonce();

                                    //savePaymentMethodToken();

                                    Log.d("paymentMethodNonce: ", paymentMethodNonce.getNonce());
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

                paymentMethodNonce = result.getPaymentMethodNonce();
                Log.d("paymentMethodNonce: ", result.getPaymentMethodNonce().getNonce());

                // use the icon and name to show in your UI
                int icon = result.getPaymentMethodType().getDrawable();
                int name = result.getPaymentMethodType().getLocalizedName();

                Log.d("user_id: ", myUser.getUid());
                Log.d("rental_id: ", thisRental.getRentalId());

                paymentMethodDescription = result.getPaymentMethodType().getCanonicalName()+" "+result.getPaymentMethodNonce().getDescription();
                txtPaymentMethod.setText(paymentMethodDescription);
                Log.d("getPaymentMethodType()", ".getCanonicalName():"+result.getPaymentMethodType().getCanonicalName());
                Log.d("getPaymentMethodType()", ".name():"+result.getPaymentMethodType().name());
                Log.d("getPaymentMethodType()", ".toString():"+result.getPaymentMethodType().toString());
                Log.d("getPaymentMethodNonce()", ".getDescription():"+result.getPaymentMethodNonce().getDescription());

                //icon.setImageResource(result.getPaymentMethodType().getDrawable());
                //icon.setImageResource(result.getPaymentMethodType().getVaultedDrawable());
                imgPayment.setImageResource(result.getPaymentMethodType().getDrawable());

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

    public void savePaymentMethodToken(){
        /* <----- Set up server with proper paymentMethodToken to use when charging ----> */
        RentalPaymentMethodNonce rpmn = new RentalPaymentMethodNonce();
        rpmn.setPaymentMethodNonce(paymentMethodNonce.getNonce());
        rpmn.setUser(myUser.getUid());

        // Send Transaction to server via Retrofit
        Call<RentalPaymentMethodNonce> call2 = braintreeEndpoint.addPaymentMethodToken(thisRental.getRentalId(), rpmn);
        call2.enqueue(new Callback<RentalPaymentMethodNonce>() {
            @Override
            public void onResponse(Call<RentalPaymentMethodNonce> call,Response<RentalPaymentMethodNonce> response) {
                int statusCode = response.code();

                // success
                Log.d("retrofit.call.enqueue", statusCode+"");
            }

            @Override
            public void onFailure(Call<RentalPaymentMethodNonce> call,Throwable t) {
                Log.d("retrofit.call.enqueue", "failed");
            }
        });

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
