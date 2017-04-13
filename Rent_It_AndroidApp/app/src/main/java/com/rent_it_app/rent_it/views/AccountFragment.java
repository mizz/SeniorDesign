package com.rent_it_app.rent_it.views;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.TextHttpResponseHandler;
import com.google.gson.Gson;
import com.rent_it_app.rent_it.Constants;
import com.rent_it_app.rent_it.R;
import com.rent_it_app.rent_it.WriteReviewActivity;
import com.rent_it_app.rent_it.json_models.User;
import com.rent_it_app.rent_it.json_models.UserEndpoint;
import com.rent_it_app.rent_it.testing.NotificationActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements LocationListener{

    private static final int REQUEST_CODE = Menu.FIRST;
    private static final String CLIENT_TOKEN_TESTING = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJiMjBmZTk5YjY0MTFmNTU4YmEwYzRjYzU3ZWE2OTBjNDYyOGI5MDIxNzNiODdkYzFhOTdkMDM5YWFhMDUxY2Q3fGNyZWF0ZWRfYXQ9MjAxNy0wMy0xOVQyMTowMTowMi41NjYzMjQ3MjArMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=";

    //private AsyncHttpClient client = new AsyncHttpClient();
    //private String clientToken;
    public static FirebaseUser myUser;
    private TextView myDisplayName, myName, myEmail,locationText;
    Retrofit retrofit;
    Gson gson;
    UserEndpoint userEndpoint;
    User myInfo;
    LocationManager locationManager;
    private Typeface ralewayRegular,aaargh,josefinsans_regular,latoLight,latoRegular;
    private TextView lblName,lblEmail,myLocation,myPhone;
    private ImageView profile;


    //private PaymentMethodNonce recentPaymentMethod;
    private final int REQUEST_PERMISSION = 1000;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // add this to each fragment so we call the BaseFragment's onCreateView
        //super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        SpannableString s = new SpannableString("PROFILE");
        s.setSpan(new TypefaceSpan("fonts/raleway_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);

        ralewayRegular = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/raleway_regular.ttf");
        aaargh = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/aaargh.ttf");
        josefinsans_regular = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/josefinsans_regular.ttf");
        latoLight = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/lato_light.ttf");
        latoRegular = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/lato_regular.ttf");


        myDisplayName = (TextView)view.findViewById(R.id.nick_name);
        myDisplayName.setTypeface(ralewayRegular);
        profile = (ImageView) view.findViewById(R.id.profile);
        /*myName = (TextView)view.findViewById(R.id.name);
        myName.setTypeface(ralewayRegular);*/
        myEmail = (TextView)view.findViewById(R.id.email);
        myEmail.setTypeface(josefinsans_regular);
        myLocation = (TextView)view.findViewById(R.id.location);
        myLocation.setTypeface(aaargh);
        myPhone = (TextView)view.findViewById(R.id.phone);
        myPhone.setTypeface(josefinsans_regular);
        //locationText = (TextView)view.findViewById(R.id.locationText);
       /* lblEmail = (TextView)view.findViewById(R.id.email_title);
        lblEmail.setTypeface(ralewayRegular);
        lblName = (TextView)view.findViewById(R.id.name_title);
        lblName.setTypeface(ralewayRegular);*/


        myUser = FirebaseAuth.getInstance().getCurrentUser();

        // how to access the typefaces
        //super.josefinsans_regular

        gson = new Gson();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userEndpoint = retrofit.create(UserEndpoint.class);

        Log.d("my uid",myUser.getUid());
        //String myUid = myUser.getUid();

        Call<User> call = userEndpoint.getUserByUid(myUser.getUid());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                int statusCode = response.code();
                Log.d("response.raw()",""+response.raw());
                myInfo = response.body();
                myDisplayName.setText(myInfo.getDisplayName());
                //myName.setText(myInfo.getFirstName()+ " "+myInfo.getLastName());
                //myEmail.setText(myInfo.getEmail());
                if(myInfo.getDisplayName().contentEquals("Mimi")){
                    myEmail.setText("mimi@gmail.com");
                    profile.setImageResource(R.drawable.female);
                }else{
                    myEmail.setText(myInfo.getEmail());
                    profile.setImageResource(R.drawable.placeholder);
                }
                Log.d("retrofit.call.enqueue", ""+statusCode);


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("retrofit.call.enqueue", t.toString());
            }

        });



        /*braintreeEndpoint = retrofit.create(BraintreeEndpoint.class);

        Call<ResponseBody> call = braintreeEndpoint.getToken(myUser.getUid());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,Response<ResponseBody> response) {
                int statusCode = response.code();
                //List<Item> items = response.body();
                try{
                    clientToken = response.body().string();
                    Log.d("myToken ",clientToken);

                    //Log.d("Client Token:", this.clientToken);
                    Log.d("Client Token:", ""+clientToken);



                    DropInResult.fetchDropInResult(getActivity(), clientToken, new DropInResult.DropInResultListener() {
                        @Override
                        public void onError(Exception exception) {
                            // an error occurred
                        }

                        @Override
                        public void onResult(DropInResult result) {
                            if (result.getPaymentMethodType() != null) {
                                // use the icon and name to show in your UI
                                int icon = result.getPaymentMethodType().getDrawable();
                                int name = result.getPaymentMethodType().getLocalizedName();

                                if (result.getPaymentMethodType() == PaymentMethodType.ANDROID_PAY) {

                                    // The last payment method the user used was Android Pay.
                                    // The Android Pay flow will need to be performed by the
                                    // user again at the time of checkout.
                                } else {
                                    // Use the payment method show in your UI and charge the user
                                    // at the time of checkout.
                                    recentPaymentMethod = result.getPaymentMethodNonce();

                                    Log.d("paymentMethodNonce: ", recentPaymentMethod.getDescription());
                                }
                            } else {
                                // there was no existing payment method
                                Log.d("braintree.paymentMethod", ".noExistingMethod");
                            }
                        }
                    });

                    Log.d("retrofit.call.enqueue", "success");
                }catch(IOException ioe){

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call,Throwable t) {
                Log.d("retrofit.call.enqueue", "failed");
            }
        });*/

        Button locationButton = (Button) view.findViewById(R.id.getLocationBtn);
        locationButton.setTypeface(latoRegular);
        locationButton.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view)
            {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                }
                else{
                    getLocation();
                }
            }

        });


        /*Button brainTreeButton = (Button) view.findViewById(R.id.braintree_button);
        brainTreeButton.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view)
            {
                startActivity(new Intent(getActivity(), WriteReviewActivity.class));
            }

        });

        Button fcmButton = (Button) view.findViewById(R.id.FCM_button);
        fcmButton.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view)
            {
                startActivity(new Intent(getActivity(), NotificationActivity.class));
            }

        });*/

        // Inflate the layout for this fragment
        return view;
    }


    void getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

                Log.d("debug", "checkSelfPermission false");
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationText.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");

                //locationStart();
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    /*public void onBraintreeSubmit(View v) {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(clientToken);
        startActivityForResult(dropInRequest.getIntent(getContext()), REQUEST_CODE);
    }*/


    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                Log.d("paymentMethodNonce: ", result.getPaymentMethodNonce().toString());

                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("resultCode: ", "RESULT_CANCELLED");
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("Braintree.DropInUI", "onActivityResult.exception");
                Log.d("Braintree.DropInUI", "onActivityResult.exception:"+error.getMessage());
                error.printStackTrace();
            }
        }
    }*/

    /*private void getToken() {
        client.get(Constants.REST_API_BASE_URL + "/api/bt/client_token/" + myUser.getUid(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                clientToken = responseString;
            }
        });
    }*/



}
