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
 * Use the {@link TutorListElement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorListElement extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String  UID = "uid";

    private String mUid;
    private Firebase mFirebase;
    private View mView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uid Parameter 1.
     * @return A new instance of fragment TutorListElement.
     */
    public static TutorListElement newInstance(String uid) {
        TutorListElement fragment = new TutorListElement();
        Bundle args = new Bundle();
        args.putString(UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    public TutorListElement() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUid = getArguments().getString(UID);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_tutor_list_element, container, false);
        mFirebase = new Firebase("https://sizzling-fire-2418.firebaseio.com/");

        mFirebase.child("users/"+mUid+"/public_profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("first_name").getValue() + " " + dataSnapshot.child("last_name").getValue();
                ((TextView) mView.findViewById(R.id.user_name)).setText(userName);
                String tutorDescription = dataSnapshot.child("tutor_description").getValue() + "";
                ((TextView) mView.findViewById(R.id.tutor_description)).setText(tutorDescription);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        return mView;
    }


}