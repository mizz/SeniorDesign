package com.rent_it_app.rent_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by malhan on 3/4/17.
 */

public class ReturnConfirmedActivity extends BaseActivity {

    private Button btnLater, btnReview;
    private TextView tvText;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_confirmed);

        tvText = (TextView)findViewById(R.id.text);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("TRADE CONFIRMED");

        /*if (getIntent().getExtras() != null) {
            String notificationType = getIntent().getExtras().get("notificationType").toString();
            if (notificationType.contentEquals("START_RENTAL")){
                tvText.setText("Your rental request was accepted. The rental clock has started!");
            }
        }*/

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
                ReturnConfirmedActivity.this.startActivity(myIntent);
            }

        });
    }

}