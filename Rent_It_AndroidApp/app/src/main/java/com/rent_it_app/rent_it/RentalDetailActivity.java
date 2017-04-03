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

import com.google.gson.Gson;
import com.rent_it_app.rent_it.firebase.Config;
import com.rent_it_app.rent_it.json_models.Rental;
import com.rent_it_app.rent_it.json_models.User;
import com.rent_it_app.rent_it.json_models.UserEndpoint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by malhan on 3/26/17.
 */

public class RentalDetailActivity extends BaseActivity{

    //private Button btnCancel,btnAccept;
    private TextView itemName, ownerName, dailyRate, returnDate,startDate;
    private String str;
    Rental myRental;
    UserEndpoint userEndpoint;
    User thisOwner;
    Gson gson;
    Retrofit retrofit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.white_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.getSupportActionBar().setTitle("RENTAL INFO");

        myRental = (Rental) getIntent().getSerializableExtra(Config.MY_RENTAL);

        gson = new Gson();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userEndpoint = retrofit.create(UserEndpoint.class);

        itemName = (TextView)findViewById(R.id.tvItem);
        ownerName = (TextView)findViewById(R.id.tvOwner);
        dailyRate = (TextView)findViewById(R.id.tvRate);
        startDate = (TextView)findViewById(R.id.tvStartDate);
        returnDate = (TextView)findViewById(R.id.tvReturnDate);


        itemName.setText(myRental.getItem().getTitle());
        ownerName.setText(myRental.getOwner());
        dailyRate.setText(myRental.getDailyRate().toString());
        str = myRental.getBookedStartDate();
        int iend = str.indexOf("T");
        if (iend != -1)
            str= str.substring(0 , iend);

        startDate.setText(str);

        str = myRental.getBookedEndDate();
        iend = str.indexOf("T");
        if (iend != -1)
            str= str.substring(0 , iend);

        returnDate.setText(str);

        Call<User> call_owner = userEndpoint.getUserByUid(myRental.getOwner());
        call_owner.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                int statusCode = response.code();
                Log.d("response.raw()",""+response.raw());
                thisOwner = response.body();
                Log.d("thisOwner: ",thisOwner.toString());
                Log.d("retrofit.call.enqueue", ""+statusCode);
                ownerName.setText(thisOwner.getDisplayName());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                ownerName.setText("");
            }

        });


       /* btnAccept = (Button)findViewById(R.id.accept_button);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });

        btnCancel = (Button)findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(RentalDetailActivity.this, HomeActivity.class);
                RentalDetailActivity.this.startActivity(myIntent);
            }
        });
*/

    }


}
