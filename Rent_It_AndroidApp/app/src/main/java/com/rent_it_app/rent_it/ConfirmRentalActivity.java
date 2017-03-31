package com.rent_it_app.rent_it;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rent_it_app.rent_it.firebase.MyFirebaseMessagingService;

/**
 * Created by malhan on 3/26/17.
 */

public class ConfirmRentalActivity extends BaseActivity{

    private Button btnCancel,btnAccept;
    private BroadcastReceiver broadcastReceiver;
    private Context context;
    private TextView itemName, renterName;
    private Bundle myData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_rental);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("CONFIRM RENTAL REQUEST");

        itemName = (TextView)findViewById(R.id.tvItem);
        renterName = (TextView)findViewById(R.id.tvRenter);

        myData = getIntent().getExtras();

        //if (getIntent().getExtras() != null) {
           /* Bundle myData = getIntent().getExtras();
            String rentalId = myData.getString("rentalId");
            String renter = myData.getString("renter");
            String itemName = myData.getString("itemName");
            Log.d("myData: ", rentalId);
        Log.d("myData: ", renter);
        Log.d("myData: ", itemName);*/
        //}
        Log.d("Testing", "opened ConfirmRentalActivity");
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("Testing", "Key: " + key + " Value: " + value);
            }


            itemName.setText(""+getIntent().getExtras().get("itemName"));
            renterName.setText(""+getIntent().getExtras().get("renter"));
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

        btnAccept = (Button)findViewById(R.id.accept_button);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });

        btnCancel = (Button)findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(ConfirmRentalActivity.this, HomeActivity.class);
                ConfirmRentalActivity.this.startActivity(myIntent);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("rentalId");
            Log.d("Testing ", message);
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
}
