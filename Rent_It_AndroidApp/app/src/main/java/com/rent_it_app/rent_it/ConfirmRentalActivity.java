package com.rent_it_app.rent_it;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rent_it_app.rent_it.json_models.Rental;
import com.rent_it_app.rent_it.json_models.RentalEndpoint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.rent_it_app.rent_it.firebase.Config.NOTIFICATION_TYPE;

/**
 * Created by malhan on 3/26/17.
 */

public class ConfirmRentalActivity extends BaseActivity{

    private Button btnCancel,btnAccept;
    private BroadcastReceiver broadcastReceiver;
    private Context context;
    private TextView itemName, renterName, estimatedProfit, returnDate;
    private Bundle myData;
    private String str, rental_id;
    Rental myRental;
    Gson gson;
    Retrofit retrofit;
    RentalEndpoint rentalEndpoint;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_rental);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("CONFIRM RENTAL REQUEST");

        itemName = (TextView)findViewById(R.id.tvItem);
        renterName = (TextView)findViewById(R.id.tvRenter);
        estimatedProfit = (TextView)findViewById(R.id.tvProfit);
        returnDate = (TextView)findViewById(R.id.tvReturnDate);

        gson = new Gson();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        rentalEndpoint = retrofit.create(RentalEndpoint.class);

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
            str = getIntent().getExtras().get("returnDate").toString();
            int iend = str.indexOf("T");
            if (iend != -1)
                str= str.substring(0 , iend);

            returnDate.setText(""+str);
            str = getIntent().getExtras().get("estimatedProfit").toString();
            Double profit = Double.parseDouble(str);
            estimatedProfit.setText("$ "+String.format("%.2f", profit));

        }else{
            itemName.setText("---");
            renterName.setText("---");
            estimatedProfit.setText("---");
            returnDate.setText("---");
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
        //update Rental Table
        Call<Rental> call = rentalEndpoint.getRentalsItemsById(rental_id);
        call.enqueue(new Callback<Rental>() {
            @Override
            public void onResponse(Call<Rental> call, Response<Rental> response) {
                int statusCode = response.code();
                myRental = response.body();
                Log.d("Testing", "" + myRental.getId());

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

                myRental.setRentalStatus(2);//renting
                TimeZone tz = TimeZone.getTimeZone("America/New_York");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                String nowAsISO = df.format(new Date());
                myRental.setRentalStartedDate(nowAsISO);

                //update Rental Table
                Call<Rental> call = rentalEndpoint.startRental(rental_id,myRental);
                call.enqueue(new Callback<Rental>() {
                    @Override
                    public void onResponse(Call<Rental> call, Response<Rental> response) {
                        int statusCode = response.code();

                        Log.d("retrofit.call.enqueue", "" + statusCode);

                        //Log.d("photo_dest!=null?", photo_destination.toString());
                        Intent myIntent = new Intent(ConfirmRentalActivity.this, TradeConfirmationSentActivity.class);
                        //myIntent.putExtra(NOTIFICATION_TYPE, "RENTAL STARTED");
                        ConfirmRentalActivity.this.startActivity(myIntent);

                    }

                    @Override
                    public void onFailure(Call<Rental> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                    }

                });


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
                        Intent myIntent = new Intent(ConfirmRentalActivity.this, HomeActivity.class);
                        //myIntent.putExtra(Config.MORE_DATA, brwsList.get(pos));
                        ConfirmRentalActivity.this.startActivity(myIntent);
                        Toast.makeText(ConfirmRentalActivity.this, "Trade Cancelled", Toast.LENGTH_LONG).show();

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


}
