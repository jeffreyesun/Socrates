package com.seffrey.socrates;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileNotLoggedIn extends Fragment {

    private String greeting;
    private Firebase firebase;
    private TextView textView;

    public ProfileNotLoggedIn() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_not_logged_in, container, false);
        greeting = "Please Connect to the Internet.";

        textView = (TextView) view.findViewById(R.id.greeting);
        textView.setText(greeting);


        firebase = new Firebase("https://sizzling-fire-2418.firebaseio.com/");
        firebase.child("values").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                greeting = dataSnapshot.child("greeting").getValue().toString();
                textView.setText(greeting);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return view;
    }


}
