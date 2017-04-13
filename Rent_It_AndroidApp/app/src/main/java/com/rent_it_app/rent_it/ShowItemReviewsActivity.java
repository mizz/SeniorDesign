package com.rent_it_app.rent_it;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.Streams;
import com.rent_it_app.rent_it.json_models.Item;
import com.rent_it_app.rent_it.json_models.Review;
import com.rent_it_app.rent_it.json_models.ReviewEndpoint;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by malhan on 3/10/17.
 */

public class ShowItemReviewsActivity extends BaseActivity{

    Retrofit retrofit;
    ReviewEndpoint reviewEndpoint;
    Gson gson;
    private ArrayList<Review> rvwList;
    private ListView irlist;
    private String itemId;
    public static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
    private static final SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT);
    private String myTitle, myDescription, myCondition, myCategory, myZipcode, myTags, myValue, myRate, myCity;
    private Typeface ralewayRegular,aaargh,josefinsans_regular,latoLight,latoRegular;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SpannableString s = new SpannableString("ITEM REVIEWS");
        s.setSpan(new TypefaceSpan("fonts/raleway_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.getSupportActionBar().setTitle(s/*category_name.toUpperCase()*/);
        toolbar.setNavigationIcon(R.drawable.white_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //this.getSupportActionBar().setTitle("Reviews");

        itemId = (String) getIntent().getSerializableExtra("ITEM_ID");
        Log.d("item id","item id: "+ itemId);

        irlist = (ListView) findViewById(R.id.browselist);

        gson = new Gson();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        reviewEndpoint = retrofit.create(ReviewEndpoint.class);

        ralewayRegular = Typeface.createFromAsset(getAssets(),  "fonts/raleway_regular.ttf");
        aaargh = Typeface.createFromAsset(getAssets(),  "fonts/aaargh.ttf");
        josefinsans_regular = Typeface.createFromAsset(getAssets(),  "fonts/josefinsans_regular.ttf");
        latoLight = Typeface.createFromAsset(getAssets(),  "fonts/lato_light.ttf");
        latoRegular = Typeface.createFromAsset(getAssets(),  "fonts/lato_regular.ttf");



        Call<ArrayList<Review>> call = reviewEndpoint.getReviewsByItemId(itemId);
        call.enqueue(new Callback<ArrayList<Review>>() {
            @Override
            public void onResponse(Call<ArrayList<Review>> call, Response<ArrayList<Review>> response) {
                int statusCode = response.code();
                rvwList = response.body();
                irlist.setAdapter(new ItemReviewAdapter());


            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {

            }

        });


    }
    private class ItemReviewAdapter extends BaseAdapter
    {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() { return rvwList.size(); }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Review getItem(int arg0)
        {
            return rvwList.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem_id(int)
         */
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2)
        {
            if (v == null)
                v = getLayoutInflater().inflate(R.layout.review_item, arg2, false);

            Review c = getItem(pos);


            LinearLayout ll = (LinearLayout) v; // get the parent layout view
            TextView title = (TextView) ll.findViewById(R.id.rTitle); // get the child text view
            TextView reviewer = (TextView)ll.findViewById(R.id.rReviewer);
            TextView date = (TextView)ll.findViewById(R.id.rDate);
            TextView comment = (TextView)ll.findViewById(R.id.rComment);
            RatingBar itemRating = (RatingBar) ll.findViewById(R.id.rRating);
            title.setText(c.getTitle());

            title.setTypeface(ralewayRegular);
            reviewer.setTypeface(josefinsans_regular);
            date.setTypeface(latoRegular);
            comment.setTypeface(latoLight);
            //Log.d("reviewer ",""+c.getRenter());
            //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS zzz");

            //SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
            //Date formattedDate = df.parse(c.getDateCreated());
            //String hiDate = c.getDateCreated();

            DateTime dateTimeObj = ISODateTimeFormat.dateTime().parseDateTime(c.getDateCreated());
            Log.d("jodatime.ISODateTime: ", dateTimeObj.toString());
            date.setText("Submitted: "+ dateTimeObj.toString( "MM/dd/yyyy"));
            //String s = c.getRenter().substring(0, 15) + "...";
            /*String s = c.getRenter();
            if (s.equals("onBNW00rlNg9S1CmBWDHTOu0j3Z2")){
                s="Mimi M";
            }else if(s.equals("DXvNYnJragb5rF64XaQ67iyfxh42")){
                s="Bonnie M";
            }else{
                s = c.getRenter();
            }*/
            reviewer.setText("by " + c.getReviewerInfo().getDisplayName());
            comment.setText(c.getItemComment());
            itemRating.setRating(c.getItemRating());

            return v;
        }

    }
}
