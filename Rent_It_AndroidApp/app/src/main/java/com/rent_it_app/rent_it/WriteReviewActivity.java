package com.rent_it_app.rent_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
    private String tempItem, tempOwner;
    FirebaseUser user;

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
                //startActivity(new Intent(getApplicationContext(), HomeActivity.class).putExtra("fragment_name", "ChatListFragment"));
                //startActivity(new Intent(this, ChatListFragment.class));
            }
        });
        WriteReviewActivity.this.getSupportActionBar().setTitle("Review");

        btnSubmit = (Button) findViewById(R.id.submit_button);
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

        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                Review new_review = new Review();
                new_review.setTitle(title.getText().toString());
                new_review.setItemComment(description.getText().toString());
                new_review.setOwnerComment(ownerDescription.getText().toString());
                new_review.setItemRating(itemRating.getNumStars());
                new_review.setOwnerRating(ownerRating.getNumStars());
                new_review.setReviewer(user.getUid());

                Call<Review> call = reviewEndpoint.addReview(new_review);
                call.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        int statusCode = response.code();

                        Log.d("retrofit.call.enqueue", "" + statusCode);

                        //Log.d("photo_dest!=null?", photo_destination.toString());

                        Toast.makeText(WriteReviewActivity.this, "Sucessfully Submitted Review", Toast.LENGTH_LONG).show();
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
