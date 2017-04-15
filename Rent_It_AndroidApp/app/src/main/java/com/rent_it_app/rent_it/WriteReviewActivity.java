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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.rent_it_app.rent_it.json_models.Review;
import com.rent_it_app.rent_it.json_models.ReviewEndpoint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by malhan on 3/9/17.
 */

public class WriteReviewActivity extends BaseActivity{

    Retrofit retrofit;
    ReviewEndpoint reviewEndpoint;
    Gson gson;
    private Button btnSubmit;
    private EditText title,description, ownerDescription;
    private RatingBar itemRating, ownerRating;
    private String tempItem, tempOwner, rental_id;
    FirebaseUser user;
    Review myReview;
    Integer renter_rating;
    private TextView lblLarge,lblSmall;
    private Typeface ralewayRegular,aaargh,josefinsans_regular,latoLight,latoRegular;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_item_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.white_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //this.getSupportActionBar().setTitle("RATE YOUR EXPERIENCE");
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
        lblSmall.setTypeface(ralewayRegular);

        btnSubmit = (Button) findViewById(R.id.submit_button);
        btnSubmit.setTypeface(latoRegular);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        ownerDescription = (EditText) findViewById(R.id.description2);
        itemRating = (RatingBar)findViewById(R.id.itemRating);
        ownerRating = (RatingBar)findViewById(R.id.ownerRating);

        //temp
        tempItem = "58c01bf46bce147b30b57c12";
        tempOwner = "PAh83NusXBabMqTY9Fxm5NQMjBp1";

        user = FirebaseAuth.getInstance().getCurrentUser();

        gson = new Gson();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        reviewEndpoint = retrofit.create(ReviewEndpoint.class);

        /*if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                rental_id= null;
            } else {
                rental_id= extras.getString("RENTAL_ID");
            }
        } else {
            rental_id= (String) savedInstanceState.getSerializable("RENTAL_ID");
        }*/

        rental_id = "7d8cd335-b2b5-4a8e-aadc-084e77ec2396";

        Log.d("rental id", rental_id);

        Call<Review> call = reviewEndpoint.getReviewByRentalId(rental_id);
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                int statusCode = response.code();
                Log.d("getReview", "" + response.raw());
                myReview = response.body();
                renter_rating = myReview.getRenterRating();
                //Toast.makeText(ReturnConfirmationSentActivity.this, "Sucessfully Submitted Review", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Log.d("getReview", t.toString());
            }

        });

        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {


                myReview.setTitle(title.getText().toString());
                myReview.setItemComment(description.getText().toString());
                myReview.setOwnerComment(ownerDescription.getText().toString());
                myReview.setItemRating(itemRating.getNumStars());
                myReview.setOwnerRating(ownerRating.getNumStars());
                myReview.setRenterRating(renter_rating);

                Call<Review> call = reviewEndpoint.updateReview(rental_id,myReview);
                call.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        int statusCode = response.code();

                        Log.d("retrofit.call.enqueue", "" + response.raw());

                        Log.d("photo_dest!=null?", ""+response.body());

                        Toast.makeText(WriteReviewActivity.this, "Sucessfully Submitted Review", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(WriteReviewActivity.this, HomeActivity.class);
                        WriteReviewActivity.this.startActivity(myIntent);
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                    }

                });


            }

        });

    }
}
