package com.rent_it_app.rent_it;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rent_it_app.rent_it.json_models.Review;
import com.rent_it_app.rent_it.json_models.ReviewEndpoint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by malhan on 3/4/17.
 */

public class ReturnConfirmationSentActivity extends BaseActivity {

    private Button btnLater,btnSubmit;
    private TextView tvText;
    private RatingBar renterRating;
    private EditText renterComment;
    private String rental_id, comment;
    private Integer rating,owner_rating,item_rating;
    Retrofit retrofit;
    ReviewEndpoint reviewEndpoint;
    Gson gson;
    Review myReview;
    private TextView lblLarge,lblSmall;
    private Typeface ralewayRegular,aaargh,josefinsans_regular,latoLight,latoRegular;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_confirmation_sent);

        tvText = (TextView)findViewById(R.id.text);
        renterRating = (RatingBar)findViewById(R.id.renterRating);
        //renterRating.setRating(0);
        renterComment = (EditText)findViewById(R.id.renterComment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SpannableString s = new SpannableString("RATE YOUR EXPERIENCE");
        s.setSpan(new TypefaceSpan("fonts/raleway_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.getSupportActionBar().setTitle(s);

        ralewayRegular = Typeface.createFromAsset(getAssets(),  "fonts/raleway_regular.ttf");
        aaargh = Typeface.createFromAsset(getAssets(),  "fonts/aaargh.ttf");
        josefinsans_regular = Typeface.createFromAsset(getAssets(),  "fonts/josefinsans_regular.ttf");
        latoLight = Typeface.createFromAsset(getAssets(),  "fonts/lato_light.ttf");
        latoRegular = Typeface.createFromAsset(getAssets(),  "fonts/lato_regular.ttf");


        lblLarge = (TextView)findViewById(R.id.lblLarge);
        lblLarge.setTypeface(ralewayRegular);
        lblSmall = (TextView)findViewById(R.id.lblSmall);
        lblSmall.setTypeface(latoLight);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                rental_id= null;
            } else {
                rental_id= extras.getString("RENTAL_ID");
            }
        } else {
            rental_id= (String) savedInstanceState.getSerializable("RENTAL_ID");
        }

        Log.d("rentalId", "" + rental_id);

        gson = new Gson();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        reviewEndpoint = retrofit.create(ReviewEndpoint.class);

        Call<Review> call = reviewEndpoint.getReviewByRentalId(rental_id);
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                int statusCode = response.code();
                Log.d("getReview", "" + response.raw());
                myReview = response.body();
                item_rating = myReview.getItemRating();
                owner_rating = myReview.getOwnerRating();
                //Toast.makeText(ReturnConfirmationSentActivity.this, "Sucessfully Submitted Review", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Log.d("getReview", t.toString());
            }

        });


        btnSubmit = (Button)findViewById(R.id.submit_button);
        btnSubmit.setTypeface(latoRegular);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                comment = renterComment.getText().toString();
                Float raw = renterRating.getRating();
                rating = raw.intValue();
                Log.d("renterRating ", ""+rating);

            if(comment.trim().equals("")){
                renterComment.requestFocus();
                renterComment.setError("Comment is required!");
                //Toast.makeText(ReturnConfirmationSentActivity.this, "Comment Empty", Toast.LENGTH_LONG).show();
            }else if(rating == 0){
                renterComment.requestFocus();
                renterComment.setError("Rating is also required!");
                //Toast.makeText(ReturnConfirmationSentActivity.this, "rating 0", Toast.LENGTH_LONG).show();
            }else {
                //Toast.makeText(ReturnConfirmationSentActivity.this, "update review", Toast.LENGTH_LONG).show();

                myReview.setRenterRating(rating);
                myReview.setRenterComment(comment);
                myReview.setOwnerRating(owner_rating);
                myReview.setItemRating(item_rating);

                Call<Review> call = reviewEndpoint.updateReview(rental_id, myReview);
                call.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        int statusCode = response.code();
                        Log.d("retrofit.call.enqueue", "" + statusCode);

                        //Log.d("photo_dest!=null?", photo_destination.toString());

                        Toast.makeText(ReturnConfirmationSentActivity.this, "Sucessfully Submitted Review", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(ReturnConfirmationSentActivity.this, HomeActivity.class);
                        ReturnConfirmationSentActivity.this.startActivity(myIntent);
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                    }

                });


            }
            }

        });

        btnLater = (Button)findViewById(R.id.later_button);
        btnLater.setTypeface(latoRegular);
        btnLater.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(ReturnConfirmationSentActivity.this, HomeActivity.class);
                ReturnConfirmationSentActivity.this.startActivity(myIntent);
            }

        });
    }

}