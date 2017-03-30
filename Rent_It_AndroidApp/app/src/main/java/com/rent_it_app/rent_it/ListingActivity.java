package com.rent_it_app.rent_it;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Rating;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.rent_it_app.rent_it.firebase.Config;
import com.rent_it_app.rent_it.json_models.Chat;
import com.rent_it_app.rent_it.json_models.Conversation;
import com.rent_it_app.rent_it.json_models.Item;
import com.rent_it_app.rent_it.json_models.Rental;
import com.rent_it_app.rent_it.json_models.RentalEndpoint;
import com.rent_it_app.rent_it.json_models.Review;
import com.rent_it_app.rent_it.json_models.ReviewEndpoint;
import com.rent_it_app.rent_it.json_models.User;
import com.rent_it_app.rent_it.json_models.UserEndpoint;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by malhan on 3/8/17.
 */

public class ListingActivity extends BaseActivity{

    Item myItem;
    User thisOwner, thisReviewer;
    Retrofit retrofit;
    ReviewEndpoint reviewEndpoint;
    RentalEndpoint rentalEndpoint;
    UserEndpoint userEndpoint;
    private Review rList;
    Gson gson;
    private TextView txtTitle, txtDescription, txtCondition, txtCity, txtRate;
    private TextView rTitle, rReviewer, rComment, oName;
    private Button readMore,startChat,ownerReviewB;
    private RatingBar itemRating, ownerRating, overallRating;
    //private ProgressDialog progress;
    private Handler mHandler = new Handler();
    private ImageView myPhoto;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager syncClient;
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private File imageFile;

    private FirebaseUser myUser;
    private Conversation convo;
    private String rental_id, ownerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        myUser = FirebaseAuth.getInstance().getCurrentUser();

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),  // getApplicationContext(),
                Constants.COGNITO_POOL_ID, // Identity Pool ID
                Regions.US_WEST_2 // Region
        );

        // Initialize the Cognito Sync client
        syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.US_WEST_2, // Region
                credentialsProvider);

        s3 = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3, getApplicationContext());

        final ProgressDialog dia = ProgressDialog.show(this, null, "Loading...");

        gson = new Gson();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        reviewEndpoint = retrofit.create(ReviewEndpoint.class);
        rentalEndpoint = retrofit.create(RentalEndpoint.class);
        userEndpoint = retrofit.create(UserEndpoint.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.white_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                /*startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                        .putExtra("fragment_name", "ChatListFragment"));*/
                //startActivity(new Intent(ListingActivity.this, BrowseActivity.class));
            }
        });
        //this.getSupportActionBar().setTitle("EDIT LISTING");

        myItem = (Item) getIntent().getSerializableExtra(Config.MORE_DATA);

        //Define
        txtTitle = (TextView) findViewById(R.id.title);
        txtDescription = (TextView)findViewById(R.id.description);
        txtCondition = (TextView)findViewById(R.id.condition);
        txtCity = (TextView)findViewById(R.id.city);
        txtRate = (TextView)findViewById(R.id.rate);
        rTitle = (TextView)findViewById(R.id.rTitle);
        rReviewer = (TextView)findViewById(R.id.rReviewer);
        oName = (TextView)findViewById(R.id.ownerName);
        rComment = (TextView)findViewById(R.id.rComment);
        readMore = (Button)findViewById(R.id.readMoreButton);
        startChat = (Button) findViewById(R.id.contact_button);
        ownerReviewB = (Button) findViewById(R.id.ownerReview);
        itemRating = (RatingBar) findViewById(R.id.rRating);
        overallRating = (RatingBar) findViewById(R.id.overallRating);
        ownerRating = (RatingBar) findViewById(R.id.ownerRating);
        myPhoto = (ImageView) findViewById(R.id.photo);


        //progress = ProgressDialog.show(this, "dialog title","dialog message", true);



        //progress.show();
        //populate fields
        txtTitle.setText(myItem.getTitle());
        txtDescription.setText(myItem.getDescription());
        txtCity.setText("" + myItem.getCity());
        txtCondition.setText("Condition : " + myItem.getCondition());

        overallRating.setRating(5);
        txtRate.setText("$" + myItem.getRate() + " /day");
        //txtRate.setText("$" + String.format("%.2f", myItem.getRate()));
        File outputDir = getApplicationContext().getCacheDir(); // context being the Activity pointer
        try {
            imageFile = File.createTempFile(myItem.getImage(), "", outputDir);
            imageFile.deleteOnExit();
            TransferObserver transferObserver =
                    transferUtility.download(Constants.BUCKET_NAME, myItem.getImage(), imageFile);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if(state == TransferState.COMPLETED) {
                        //myPhoto.setImageResource(R.drawable.bg);
                        Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        myPhoto.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 900, 600, false));
                    }
                }

                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    // Do something in the callback.
                }

                public void onError(int id, Exception e) {
                    // Do something in the callback.
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //get owner display name
        //String ownerId = myItem.getUid();
        Log.d("ownerId: ",myItem.getUid());
        //oName.setText(myItem.getUid());
        //oName.setText("Edward James");

        Call<User> call_owner = userEndpoint.getUserByUid(myItem.getUid());
        call_owner.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                int statusCode = response.code();
                Log.d("response.raw()",""+response.raw());
                thisOwner = response.body();
                Log.d("thisOwner: ",thisOwner.toString());
                Log.d("retrofit.call.enqueue", ""+statusCode);
                ownerName = thisOwner.getDisplayName();
                oName.setText(ownerName);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ownerName = "";
                oName.setText("Unknown");

            }

        });



        //String itemId = myItem.getId();

        Call<Review> call = reviewEndpoint.getLatestReviewByItemId(myItem.getId());
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {

                int statusCode = response.code();
                rList = response.body();

             /*   Log.d("response ",""+response);
                Log.d("response.body() ",""+response.body());
                Log.d("rList ",""+rList);
                Log.d("response.raw()",""+response.raw());
                Log.d("response.toString() ",""+response);*/


                rTitle.setText(rList.getTitle());
                Log.d("getItemRating() ","" + rList.getItemRating());
                Log.d("getOwnerRating() ","" + rList.getOwnerRating());
                itemRating.setRating(rList.getItemRating());
                ownerRating.setRating(rList.getOwnerRating());
                //rReviewer.setText("by "+rList.getReviewer());
                rReviewer.setText("by "+rList.getReviewerInfo().getDisplayName());

                String s = rList.getItemComment();
                if (s.length() > 100) {
                    s = s.substring(0, 100) + "...";
                }
                rComment.setText(s);
                Log.d("retrofit.call.enqueue", ""+statusCode);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        dia.dismiss();
                    }
                }, 1000);
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {

                rComment.setText("No review available");
                readMore.setVisibility(View.GONE);
                rTitle.setVisibility(View.GONE);
                rReviewer.setVisibility(View.GONE);
                itemRating.setVisibility(View.GONE);

                Log.d("retrofit.call.enqueue", "failed "+t);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        dia.dismiss();
                    }
                }, 1000);
            }

        });

        //get reviewer display name - remove if able to get display name from server
        /*Call<User> call_reviewer = userEndpoint.getUserByUid(myItem.getUid());
        call_reviewer.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                int statusCode = response.code();
                Log.d("response.raw()",""+response.raw());
                thisReviewer = response.body();
                Log.d("thisReviewer: ",thisReviewer.toString());
                Log.d("retrofit.call.enqueue", ""+statusCode);
                rReviewer.setText(thisReviewer.getUid());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                rReviewer.setText("");
                Log.d("retrofit.call.enqueue", "failed "+t);

            }

        });*/

        //progress.dismiss();
        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myFancyMethod(v);
                Intent myIntent = new Intent(ListingActivity.this, ShowItemReviewsActivity.class);
                myIntent.putExtra("ITEM_ID", rList.getItem());
                ListingActivity.this.startActivity(myIntent);
            }
        });

        ownerReviewB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(ListingActivity.this, ShowOwnerReviewsActivity.class);
                myIntent.putExtra("OWNER_ID", rList.getOwner());
                ListingActivity.this.startActivity(myIntent);
            }
        });

        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rental_id = UUID.randomUUID().toString();

                Date msgDate = DateTime.now().toDate();
                Chat defaultFirstMsg = new Chat();
                defaultFirstMsg.setDate(msgDate);
                defaultFirstMsg.setSender(myUser.getUid());
                defaultFirstMsg.setReceiver(myItem.getUid());
                defaultFirstMsg.setStatus(Chat.STATUS_SENDING);
                String defaultMsg = "Hi " + ownerName +
                        //myItem.getUid()+
                         ", I'm interested in renting your "
                                  + myItem.getTitle() + ".";
                defaultFirstMsg.setMsg(defaultMsg);

                convo = new Conversation();
                convo.setRenter(myUser.getUid());
                convo.setOwner(myItem.getUid());
                convo.setItem_id(myItem.getId());
                convo.setItem_name(myItem.getTitle());
                convo.setRental_id(rental_id);

                convo.setLastMsgDate(msgDate);
                ArrayList<Chat> chatMsgs = new ArrayList<Chat>();
                chatMsgs.add(defaultFirstMsg);
                convo.setChat(chatMsgs);



                // TODO: Fill in "fill-in-rental-id" with the rental-id as appropriate
                FirebaseDatabase.getInstance().getReference("conversations").child(convo.getRental_id())
                        .setValue(convo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       if (task.isSuccessful()) {
                                                           convo.getChat().get(0).setStatus(Chat.STATUS_SENT);
                                                       } else {
                                                           convo.getChat().get(0).setStatus(Chat.STATUS_FAILED);
                                                       }
                                                       FirebaseDatabase.getInstance()
                                                               .getReference("conversations")
                                                               .child(convo.getRental_id())
                                                               .child("chat")
                                                               //.setValue(convo);
                                                               .child("0")
                                                               .setValue(convo.getChat().get(0));

                                                   }
                                               }
                        );


                Rental newRental = new Rental();
                newRental.setRentalId(rental_id);
                newRental.setRenter(myUser.getUid());
                newRental.setOwner(myItem.getUid());
                newRental.setItem(myItem);
                newRental.setRentalStatus(1);//1 means contacted but not rented. 2 is rented and 3 is returned.0 means remove from trade list

                Call<Rental> call = rentalEndpoint.addRental(newRental);
                call.enqueue(new Callback<Rental>() {
                    @Override
                    public void onResponse(Call<Rental> call, Response<Rental> response) {
                        int statusCode = response.code();

                        Log.d("retrofit.call.enqueue", "" + statusCode);

                    }

                    @Override
                    public void onFailure(Call<Rental> call, Throwable t) {
                        Log.d("retrofit.call.enqueue", t.toString());
                    }

                });

                Intent myIntent = new Intent(ListingActivity.this, ChatActivity.class);
                myIntent.putExtra(Config.EXTRA_DATA, convo);
                ListingActivity.this.startActivity(myIntent);
            }
        });

    }



}
