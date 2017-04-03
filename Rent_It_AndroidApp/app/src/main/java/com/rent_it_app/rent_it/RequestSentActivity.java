package com.rent_it_app.rent_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Created by malhan on 3/4/17.
 */

public class RequestSentActivity extends BaseActivity {

    private Button btnHome;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_sent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("REQUEST SENT");

        btnHome = (Button)findViewById(R.id.btn_home);
        btnHome.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(RequestSentActivity.this, HomeActivity.class);
                RequestSentActivity.this.startActivity(myIntent);
            }

        });
    }

}