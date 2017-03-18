package com.rent_it_app.rent_it.views;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rent_it_app.rent_it.ChatActivity;
import com.rent_it_app.rent_it.R;
import com.rent_it_app.rent_it.SignInActivity;
import com.rent_it_app.rent_it.firebase.Config;
import com.rent_it_app.rent_it.json_models.ChatUser;
import com.rent_it_app.rent_it.json_models.Conversation;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeywordsFragment extends Fragment {

    private ArrayList<String> keywordList;

    public ListView list;

    public KeywordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keywords, container, false);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("CHAT LIST");

        keywordList = new ArrayList<>();
        keywordList.add("Baby Stroller");
        keywordList.add("Basketball");
        keywordList.add("Badminton");
        keywordList.add("Bass Guitar");

        Log.d("KeywordsFragment: ", "onCreateView");

        String searchText = getArguments().getString("searchText");
        Log.d("KeywordsFragment: ", "searchText: " + searchText);

        list = (ListView) view.findViewById(R.id.list);

        //ListView list = (ListView) view.findViewById(R.id.list);
        list.setAdapter(new KeywordListAdapter());
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0,
                                    View arg1, int pos, long arg3) {
                // Perform query here to database for matching items...

            }
        });

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

    }

    private class KeywordListAdapter extends BaseAdapter
    {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        //public int getCount() { return uList.size(); }
        public int getCount() { return keywordList.size(); }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        //public ChatUser getItem(int arg0){return uList.get(arg0);}
        public String getItem(int arg0)
        {
            return keywordList.get(arg0);
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
                v = getActivity().getLayoutInflater().inflate(R.layout.chat_item, arg2, false);

            String keyword = getItem(pos);

            TextView lbl = (TextView) v;
            lbl.setText(keyword);

            return v;
        }

    }

}
