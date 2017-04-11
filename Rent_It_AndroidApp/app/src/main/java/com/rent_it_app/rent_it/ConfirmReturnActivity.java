package com.rent_it_app.rent_it;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rent_it_app.rent_it.json_models.BraintreeEndpoint;
import com.rent_it_app.rent_it.json_models.GenericResult;
import com.rent_it_app.rent_it.json_models.Rental;
import com.rent_it_app.rent_it.json_models.RentalEndpoint;
import com.rent_it_app.rent_it.json_models.TransactionAmount;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by malhan on 3/26/17.
 */

public class ConfirmReturnActivity extends BaseActivity{

    private Button btnCancel,btnAccept;
    private Double diff;
    private Long days,hours,diff2;
    private Context context;
    private TextView itemName, renterName, profit, rentalPeriod,notes;
    private Bundle myData;
    private String str, rental_id;
    Rental myRental;
    Gson gson;
    Retrofit retrofit;
    RentalEndpoint rentalEndpoint;
    BraintreeEndpoint braintreeEndpoint;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_return);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("CONFIRM RENTAL REQUEST");

        itemName = (TextView)findViewById(R.id.tvItem);
        renterName = (TextView)findViewById(R.id.tvRenter);
        profit = (TextView)findViewById(R.id.tvProfit);
        rentalPeriod = (TextView)findViewById(R.id.tvRentalPeriod);
        notes = (TextView)findViewById(R.id.tvNotes);

        gson = new Gson();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        rentalEndpoint = retrofit.create(RentalEndpoint.class);
        braintreeEndpoint = retrofit.create(BraintreeEndpoint.class);

        //myData = getIntent().getExtras();

        //if (getIntent().getExtras() != null) {
           /* Bundle myData = getIntent().getExtras();
            String rentalId = myData.getString("rentalId");
            String renter = myData.getString("renter");
            String itemName = myData.getString("itemName");
            Log.d("myData: ", rentalId);
        Log.d("myData: ", renter);
        Log.d("myData: ", itemName);*/
        //}
        //Log.d("Testing", "opened ConfirmRentalActivity");
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("Testing", "Key: " + key + " Value: " + value);
            }

            rental_id = getIntent().getExtras().get("rentalId").toString();
            Log.d("rentalId: ", rental_id);
            itemName.setText(""+getIntent().getExtras().get("itemName"));
            renterName.setText(""+getIntent().getExtras().get("renter"));
            /*str = getIntent().getExtras().get("returnDate").toString();
            int iend = str.indexOf("T");
            if (iend != -1)
                str= str.substring(0 , iend);

            returnDate.setText(""+str);
            str = getIntent().getExtras().get("estimatedProfit").toString();
            Double profit = Double.parseDouble(str);
            estimatedProfit.setText("$ "+String.format("%.2f", profit));*/

        }else{
            itemName.setText("---");
            renterName.setText("---");
            //estimatedProfit.setText("---");
            //returnDate.setText("---");
        }
        //see braodcast works
        /*broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("rentalId");
                Log.d("Testing ", message);
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.DATA_BROADCAST));
*/
        //get rental infomation
        Call<Rental> call = rentalEndpoint.getRentalsItemsById(rental_id);
        call.enqueue(new Callback<Rental>() {
            @Override
            public void onResponse(Call<Rental> call, Response<Rental> response) {
                int statusCode = response.code();
                myRental = response.body();
                Log.d("Testing", "" + myRental.getId());
                diff = myRental.getRentalPeriod();
                diff2 = diff.longValue();
                hours = (diff2 / (60 * 60 * 1000));
                days = (diff2 / (24 * 60 * 60 * 1000));
                diff2 = hours - (24*days);
                rentalPeriod.setText(days+" days "+diff2+" hours");
                profit.setText("$ "+roundTwoDecimals(myRental.getRentalFee()));
                if (!myRental.getNotes().contentEquals(""))
                    notes.setText(myRental.getNotes());

            }

            @Override
            public void onFailure(Call<Rental> call, Throwable t) {
                Log.d("retrofit.call.enqueue", t.toString());
            }

        });



        btnAccept = (Button)findViewById(R.id.accept_button);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                myRental.setRentalStatus(3);//renting
                TimeZone tz = TimeZone.getTimeZone("America/New_York");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                String nowAsISO = df.format(new Date());
                myRental.setReturnConfirmedDate(nowAsISO);

                //update Rental Table
                Call<Rental> call = rentalEndpoint.confirmReturn(rental_id,myRental);
                call.enqueue(new Callback<Rental>() {
                    @Override
                    public void onResponse(Call<Rental> call, Response<Rental> response) {
                        int statusCode = response.code();

                        Log.d("retrofit.call.enqueue", "" + statusCode);

                        Log.d("Testing ", "sucess");
                        Intent myIntent = new Intent(ConfirmReturnActivity.this, ReturnConfirmationSentActivity.class);
                        myIntent.putExtra("RENTAL_ID", rental_id);
                        ConfirmReturnActivity.this.startActivity(myIntent);

                    }

                    @Override
                    public void onFailure(Call<Rental> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                        Log.d("Testing ", "fail");
                    }

                });

                /*<----- Finish charging now that the lender has confirmed return of item ----->*/
                TransactionAmount transactionAmount = new TransactionAmount();
                transactionAmount.setChargeAmount(roundTwoDecimals(myRental.getTotal()));
                Call<GenericResult> call2 = braintreeEndpoint.processTransaction(myRental.getRentalId(),transactionAmount);
                call2.enqueue(new Callback<GenericResult>() {
                    @Override
                    public void onResponse(Call<GenericResult> call, Response<GenericResult> response) {
                        int statusCode = response.code();

                        Log.d("retrofit.call.enqueue", "" + statusCode);
                        Log.d("processTransaction:", response.body().getResult());

                        if(response.body().getResult().equals("true")){
                            Toast.makeText(ConfirmReturnActivity.this, "Successfully Charged!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(ConfirmReturnActivity.this, "Failed to Charge!", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<GenericResult> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                        Log.d("Testing ", "fail");
                    }

                });
/*

                Intent myIntent = new Intent(ConfirmReturnActivity.this, ReturnConfirmationSentActivity.class);
                ConfirmReturnActivity.this.startActivity(myIntent);
*/


            }
        });

        btnCancel = (Button)findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //update Rental Table
                Call<ResponseBody> call = rentalEndpoint.cancelRequest(rental_id);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        int statusCode = response.code();

                        Log.d("retrofit.call.enqueue", "" + statusCode);

                        //Log.d("photo_dest!=null?", photo_destination.toString());
                        Intent myIntent = new Intent(ConfirmReturnActivity.this, HomeActivity.class);
                        //myIntent.putExtra(Config.MORE_DATA, brwsList.get(pos));
                        ConfirmReturnActivity.this.startActivity(myIntent);
                        Toast.makeText(ConfirmReturnActivity.this, "Return Canceled", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                    }

                });

                /*Intent myIntent = new Intent(ConfirmRentalActivity.this, HomeActivity.class);
                ConfirmRentalActivity.this.startActivity(myIntent);*/
            }
        });


    }
    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }


}
