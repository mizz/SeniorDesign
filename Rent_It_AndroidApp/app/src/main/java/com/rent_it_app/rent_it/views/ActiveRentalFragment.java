package com.rent_it_app.rent_it.views;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.rent_it_app.rent_it.Constants;
import com.rent_it_app.rent_it.EditItemActivity;
import com.rent_it_app.rent_it.R;
import com.rent_it_app.rent_it.firebase.Config;
import com.rent_it_app.rent_it.json_models.Item;
import com.rent_it_app.rent_it.json_models.ItemEndpoint;
import com.rent_it_app.rent_it.json_models.Rental;
import com.rent_it_app.rent_it.json_models.RentalEndpoint;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveRentalFragment extends Fragment {


    public ActiveRentalFragment() {
        // Required empty public constructor
    }

    Retrofit retrofit;
    RentalEndpoint rentalEndpoint;
    TextView tv1;
    Gson gson;
    private ArrayList<Rental> rList;
    private ListView list;
    CognitoCachingCredentialsProvider credentialsProvider;
    CognitoSyncManager syncClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active_rental, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("RENTAL");

        gson = new Gson();
        tv1 = (TextView)view.findViewById(R.id.textView1);
        list = (ListView) view.findViewById(R.id.list);

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        rentalEndpoint = retrofit.create(RentalEndpoint.class);

        /*credentialsProvider = new CognitoCachingCredentialsProvider(
                getContext(),  // getApplicationContext(),
                Constants.COGNITO_POOL_ID, // Identity Pool ID
                Regions.US_WEST_2 // Region
        );

        // Initialize the Cognito Sync client
        syncClient = new CognitoSyncManager(
                getContext(),
                Regions.US_WEST_2, // Region
                credentialsProvider);*/

        return view;
    }
    /* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    public void onResume()
    {
        super.onResume();
        loadItemList();

    }
    private void loadItemList() {

        rList = new ArrayList<Rental>();
        FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();



        Call<ArrayList<Rental>> call = rentalEndpoint.getItemsByUid(myUser.getUid());
        call.enqueue(new Callback<ArrayList<Rental>>() {
            @Override
            public void onResponse(Call<ArrayList<Rental>> call, Response<ArrayList<Rental>> response) {
                int statusCode = response.code();
                //List<Item> items = response.body();
                rList = response.body();


                //tv1.setText(sb.toString());
                list.setAdapter(new ItemListAdapter());
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0,
                                            View arg1, int pos, long arg3) {
                        startActivity(new Intent(getActivity(), EditItemActivity.class)
                                .putExtra(Config.EXTRA_DATA, rList.get(pos)));
                    }
                });

                Log.d("retrofit.call.enqueue", ""+statusCode);
            }

            @Override
            public void onFailure(Call<ArrayList<Rental>> call, Throwable t) {
                Log.d("retrofit.call.enqueue", "failed");
            }
        });

    }
    private class ItemListAdapter extends BaseAdapter
    {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() { return rList.size(); }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Rental getItem(int arg0)
        {
            return rList.get(arg0);
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
                v = getActivity().getLayoutInflater().inflate(R.layout.inventory_item, arg2, false);

            Rental c = getItem(pos);

            TextView lbl = (TextView) v;
            lbl.setText(c.getItem().getTitle());

            /*lbl.setCompoundDrawablesWithIntrinsicBounds(
                    c.isOnline() ? R.drawable.ic_online
                            : R.drawable.ic_offline, 0, R.drawable.arrow, 0);*/

            return v;
        }

    }


}
