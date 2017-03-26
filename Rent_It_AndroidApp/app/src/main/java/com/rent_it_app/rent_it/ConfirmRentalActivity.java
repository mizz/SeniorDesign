package com.rent_it_app.rent_it;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/**
 * Created by malhan on 3/26/17.
 */

public class ConfirmRentalActivity extends BaseActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_rental);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("CONFIRM RENTAL REQUEST");
    }
}
