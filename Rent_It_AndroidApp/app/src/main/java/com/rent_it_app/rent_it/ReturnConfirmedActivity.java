package com.rent_it_app.rent_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_confirmed);

        tvText = (TextView)findViewById(R.id.text);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("RETURN CONFIRMED");

        if (getIntent().getExtras() != null) {
            rental_id = getIntent().getExtras().get("rentalId").toString();
        }else{
            rental_id = "";
        }

        Log.d("rental id", rental_id);

        btnLater = (Button)findViewById(R.id.later_button);
        btnLater.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(ReturnConfirmedActivity.this, HomeActivity.class);
                ReturnConfirmedActivity.this.startActivity(myIntent);
            }

        });

        btnReview = (Button)findViewById(R.id.review_button);
        btnReview.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(ReturnConfirmedActivity.this, WriteReviewActivity.class);
                myIntent.putExtra("RENTAL_ID", rental_id);
                ReturnConfirmedActivity.this.startActivity(myIntent);
            }

        });
    }

}