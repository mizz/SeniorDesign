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
import android.widget.TextView;

/**
 * Created by malhan on 3/4/17.
 */

public class ReturnConfirmedActivity extends BaseActivity {

    private Button btnLater, btnReview;
    private TextView tvText;
    private String rental_id;
    private TextView lblLarge,lblPolicy,lblText;
    private Typeface ralewayRegular,aaargh,josefinsans_regular,latoLight,latoRegular;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_confirmed);

        tvText = (TextView)findViewById(R.id.text);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //this.getSupportActionBar().setTitle("RETURN CONFIRMATION");
        SpannableString s = new SpannableString("RETURN CONFIRMATION");
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
        lblPolicy = (TextView)findViewById(R.id.policy);
        lblPolicy.setTypeface(latoLight);
        lblText = (TextView)findViewById(R.id.text);
        lblText.setTypeface(latoLight);

        if (getIntent().getExtras() != null) {
            rental_id = getIntent().getExtras().get("rentalId").toString();
        }else{
            rental_id = "";
        }

        Log.d("rental id", rental_id);

        btnLater = (Button)findViewById(R.id.later_button);
        btnLater.setTypeface(latoRegular);
        btnLater.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(ReturnConfirmedActivity.this, HomeActivity.class);
                ReturnConfirmedActivity.this.startActivity(myIntent);
            }

        });

        btnReview = (Button)findViewById(R.id.review_button);
        btnReview.setTypeface(latoRegular);
        btnReview.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(ReturnConfirmedActivity.this, WriteReviewActivity.class);
                myIntent.putExtra("RENTAL_ID", rental_id);
                ReturnConfirmedActivity.this.startActivity(myIntent);
            }

        });
    }

}