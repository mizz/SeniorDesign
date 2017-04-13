package com.rent_it_app.rent_it.views;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.rent_it_app.rent_it.ChatActivity;
import com.rent_it_app.rent_it.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.chatt.demo.custom.CustomActivity;
import com.rent_it_app.rent_it.SignInActivity;
import com.rent_it_app.rent_it.firebase.Config;
import com.rent_it_app.rent_it.json_models.ChatUser;
//import com.chatt.demo.utils.Const;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rent_it_app.rent_it.json_models.Conversation;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {

    /** Users database reference */
    private DatabaseReference mFirebaseDatabaseReference;

    /** The Chat list. */
    //private ArrayList<ChatUser> uList;
    private ArrayList<Conversation> cList;

    // Todo: delete once switch to use Conversation instead of ChatMessage class
    public static ChatUser user;
    public static FirebaseUser myUser;
    private Typeface ralewayRegular,aaargh,josefinsans_regular,latoLight,latoRegular;

    public ListView list;


    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        SpannableString s = new SpannableString("CHAT LIST");
        s.setSpan(new TypefaceSpan("fonts/raleway_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);

        list = (ListView) view.findViewById(R.id.list);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        ralewayRegular = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/raleway_regular.ttf");
        aaargh = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/aaargh.ttf");
        josefinsans_regular = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/josefinsans_regular.ttf");
        latoLight = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/lato_light.ttf");
        latoRegular = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/lato_regular.ttf");



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
        loadChatList();

    }

    private void loadChatList()
    {
        final ProgressDialog dia = ProgressDialog.show(getActivity(), null, "Loading...");

        // Pull the users list once no sync required.
        //mFirebaseDatabaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
        mFirebaseDatabaseReference.child("conversations").orderByChild("last_active").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {dia.dismiss();

                myUser = FirebaseAuth.getInstance().getCurrentUser();
                if(myUser != null) {
                    long size = dataSnapshot.getChildrenCount();
                    if (size == 0) {
                        Toast.makeText(getActivity(), "No chat found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //uList = new ArrayList<ChatUser>();
                    cList = new ArrayList<Conversation>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        //ChatUser user = ds.getValue(ChatUser.class);
                        Conversation conversation = ds.getValue(Conversation.class);

                        //Logger.getLogger(ChatListFragment.class.getName()).log(Level.ALL,user.getUsername());
                        //Log.d("Test", "conversation.getOwner(): "+ conversation.getOwner());
                        //Log.d("Test", "conversation.getItem_name(): "+ conversation.getItem_name());

                        /*if (!user.getId().contentEquals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            uList.add(user);*/
                        if (conversation.getOwner().contentEquals(myUser.getUid()) || conversation.getRenter()
                                .contentEquals(myUser.getUid())) {
                            cList.add(conversation);



                            /*if (lastMsgDate == null || lastMsgDate.before(conversation.getDate()))
                                lastMsgDate = conversation.getDate();*/
                            //Log.d("Test2", "conversation.getOwner(): "+ conversation.getOwner());
                            //Log.d("Test2", "conversation.getItem_name(): "+ conversation.getItem_name());
                            //adp.notifyDataSetChanged();
                        }
                        Collections.reverse(cList);
                    }
                    //ListView list = (ListView) view.findViewById(R.id.list);
                    list.setAdapter(new ChatListAdapter());
                    list.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0,
                                                View arg1, int pos, long arg3) {
                            startActivity(new Intent(getActivity(), ChatActivity.class)
                                    .putExtra(Config.EXTRA_DATA, cList.get(pos)));
                        }
                    });

                }else{
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * The Class ChatListAdapter is the adapter class for User ListView. This
     * adapter shows the user name and it's only online status for each item.
     */
    private class ChatListAdapter extends BaseAdapter
    {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        //public int getCount() { return uList.size(); }
        public int getCount() { return cList.size(); }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        //public ChatUser getItem(int arg0){return uList.get(arg0);}
        public Conversation getItem(int arg0)
        {
            return cList.get(arg0);
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

            //ChatUser c = getItem(pos);
            Conversation c = getItem(pos);

            LinearLayout ll = (LinearLayout) v;

            TextView lbl = (TextView) ll.findViewById(R.id.itemName);
            lbl.setTypeface(latoLight);
            TextView lblTime = (TextView) ll.findViewById(R.id.time);
            lblTime.setTypeface(ralewayRegular);
            TextView lblFrom = (TextView) ll.findViewById(R.id.from);
            lblFrom.setTypeface(aaargh);
            //lbl.setText(c.getUsername());
            lbl.setText(c.getItem_name());
            lblTime.setText(DateUtils.getRelativeDateTimeString(getActivity(), c
                            .getLastMsgDate().getTime(), DateUtils.SECOND_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS, 0));
            if(myUser.getUid().contentEquals(c.getRenter())) {
                lblFrom.setText(c.getRenterName());
            }else{
                lblFrom.setText(c.getOwnerName());
            }
            /*lbl.setCompoundDrawablesWithIntrinsicBounds(
                    c.isOnline() ? R.drawable.ic_online
                            : R.drawable.ic_offline, 0, R.drawable.arrow, 0);*/

            return v;
        }

    }

}
