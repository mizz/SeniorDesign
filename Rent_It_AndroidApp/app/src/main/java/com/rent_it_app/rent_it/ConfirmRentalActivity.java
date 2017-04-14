package com.rent_it_app.rent_it;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rent_it_app.rent_it.json_models.Rental;
import com.rent_it_app.rent_it.json_models.RentalEndpoint;
import com.rent_it_app.rent_it.json_models.Review;
import com.rent_it_app.rent_it.json_models.ReviewEndpoint;

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
    private TextView itemName, renterName, estimatedProfit, returnDate, notes;
    private Bundle myData;
    private String str, rental_id;
    Rental myRental;
    Gson gson;
    Retrofit retrofit;
    RentalEndpoint rentalEndpoint;
    ReviewEndpoint reviewEndpoint;
    private Typeface ralewayRegular,aaargh,josefinsans_regular,latoLight,latoRegular;
    private TextView lblItem,lblRenter,lblReturn,lblProfit,lblNotes,policy,text;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_rental);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SpannableString s = new SpannableString("CONFIRM RENTAL REQUEST");
        s.setSpan(new TypefaceSpan("fonts/raleway_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.getSupportActionBar().setTitle(s);

        ralewayRegular = Typeface.createFromAsset(getAssets(),  "fonts/raleway_regular.ttf");
        aaargh = Typeface.createFromAsset(getAssets(),  "fonts/aaargh.ttf");
        josefinsans_regular = Typeface.createFromAsset(getAssets(),  "fonts/josefinsans_regular.ttf");
        latoLight = Typeface.createFromAsset(getAssets(),  "fonts/lato_light.ttf");
        latoRegular = Typeface.createFromAsset(getAssets(),  "fonts/lato_regular.ttf");


        itemName = (TextView)findViewById(R.id.tvItem);
        renterName = (TextView)findViewById(R.id.tvRenter);
        estimatedProfit = (TextView)findViewById(R.id.tvProfit);
        returnDate = (TextView)findViewById(R.id.tvReturnDate);
        notes = (TextView)findViewById(R.id.tvNotes);

        itemName.setTypeface(ralewayRegular);
        renterName.setTypeface(ralewayRegular);
        estimatedProfit.setTypeface(ralewayRegular);
        renterName.setTypeface(ralewayRegular);
        notes.setTypeface(ralewayRegular);

        lblItem = (TextView)findViewById(R.id.lblItem);
        lblRenter = (TextView)findViewById(R.id.lblRenter);
        lblReturn = (TextView)findViewById(R.id.lblReturn);
        lblProfit = (TextView)findViewById(R.id.lblProfit);
        lblNotes = (TextView)findViewById(R.id.lblNotes);
        lblItem.setTypeface(latoRegular);
        lblRenter.setTypeface(latoRegular);
        lblReturn.setTypeface(latoRegular);
        lblProfit.setTypeface(latoRegular);
        lblNotes.setTypeface(latoRegular);

        policy = (TextView)findViewById(R.id.policy);
        policy.setTypeface(latoRegular);
        text = (TextView)findViewById(R.id.text);
        text.setTypeface(latoLight);

        gson = new Gson();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        rentalEndpoint = retrofit.create(RentalEndpoint.class);
        reviewEndpoint = retrofit.create(ReviewEndpoint.class);

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
        Intent checkingIntent = getIntent();
        Bundle checkingExtras = getIntent().getExtras();
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
                /*if(!myRental.getNotes().contentEquals(""))
                    notes.setText(myRental.getNotes());*/

            }

            @Override
            public void onFailure(Call<Rental> call, Throwable t) {
                Log.d("retrofit.call.enqueue", t.toString());
            }

        });



        btnAccept = (Button)findViewById(R.id.accept_button);
        btnAccept.setTypeface(ralewayRegular);
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
                        /*Intent myIntent = new Intent(ConfirmRentalActivity.this, TradeConfirmationSentActivity.class);
                        ConfirmRentalActivity.this.startActivity(myIntent);*/

                    }

                    @Override
                    public void onFailure(Call<Rental> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                    }

                });

                Review newReview = new Review();

                newReview.setRentalId(rental_id);
                newReview.setItem(myRental.getItem().getId());
                newReview.setOwner(myRental.getOwner());
                newReview.setRenter(myRental.getRenter());
                newReview.setOwnerRating(0);
                newReview.setOwnerComment("");
                newReview.setItemRating(0);
                newReview.setItemComment("");
                newReview.setTitle("");
                newReview.setRenterRating(0);
                newReview.setRenterComment("");
                Call<Review> call2 = reviewEndpoint.addReview(newReview);
                call2.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        int statusCode = response.code();

                        Log.d("retrofit.call.enqueue", "" + statusCode);

                        //Log.d("photo_dest!=null?", photo_destination.toString());
                        Intent myIntent = new Intent(ConfirmRentalActivity.this, TradeConfirmationSentActivity.class);
                        //myIntent.putExtra(NOTIFICATION_TYPE, "RENTAL STARTED");
                        ConfirmRentalActivity.this.startActivity(myIntent);

                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                    }

                });

            }
        });

        btnCancel = (Button)findViewById(R.id.cancel_button);
        btnCancel.setTypeface(ralewayRegular);
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
