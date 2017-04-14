package com.rent_it_app.rent_it;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by malhan on 3/4/17.
 */

public class RequestSentActivity extends BaseActivity {

    private Button btnHome;
    private TextView lblLarge,lblSmall;
    private Typeface ralewayRegular,aaargh,josefinsans_regular,latoLight,latoRegular;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_sent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SpannableString s = new SpannableString("REQUEST SENT TO OWNER");
        s.setSpan(new TypefaceSpan("fonts/raleway_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.getSupportActionBar().setTitle(s/*category_name.toUpperCase()*/);

        ralewayRegular = Typeface.createFromAsset(getAssets(),  "fonts/raleway_regular.ttf");
        aaargh = Typeface.createFromAsset(getAssets(),  "fonts/aaargh.ttf");
        josefinsans_regular = Typeface.createFromAsset(getAssets(),  "fonts/josefinsans_regular.ttf");
        latoLight = Typeface.createFromAsset(getAssets(),  "fonts/lato_light.ttf");
        latoRegular = Typeface.createFromAsset(getAssets(),  "fonts/lato_regular.ttf");

        lblLarge = (TextView)findViewById(R.id.lblLarge);
        lblLarge.setTypeface(ralewayRegular);
        lblSmall = (TextView)findViewById(R.id.lblSmall);
        lblSmall.setTypeface(latoLight);

        btnHome = (Button)findViewById(R.id.btn_home);
        btnHome.setTypeface(ralewayRegular);
        btnHome.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(RequestSentActivity.this, HomeActivity.class);
                RequestSentActivity.this.startActivity(myIntent);
            }

        });
    }

}