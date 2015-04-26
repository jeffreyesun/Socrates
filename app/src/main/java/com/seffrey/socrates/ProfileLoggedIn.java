package com.seffrey.socrates;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileLoggedIn extends Fragment {
    private Firebase firebase;
    private String userName;
    private String userDescription;
    private Boolean isTutor;
    private String tutorDescription;
    private View mView;


    public ProfileLoggedIn() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment, initialize variables */
        mView = inflater.inflate(R.layout.fragment_profile_logged_in, container, false);
        mView.findViewById(R.id.update_button).setOnClickListener(new updateButtonListener());
        mView.findViewById(R.id.refresh_button).setOnClickListener(new refreshButtonListener());
        ((CheckBox) mView.findViewById(R.id.is_tutor)).setOnCheckedChangeListener(new checkBoxListener());
        firebase = new Firebase("https://sizzling-fire-2418.firebaseio.com/");

        /* Refresh profile once the user is logged in to Firebase */
        firebase.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData == null) {
                    ((EditText) mView.findViewById(R.id.user_name)).setText("Server Error");
                    ((EditText) mView.findViewById(R.id.user_description)).setText("Server Error");
                    ((EditText) mView.findViewById(R.id.tutor_description)).setText("Server Error");
                    ((CheckBox) mView.findViewById(R.id.is_tutor)).setChecked(false);
                } else {
                    firebase.child("users/" + firebase.getAuth().getUid() + "/public_profile").addListenerForSingleValueEvent(new profileRefresh());
                }
            }
        });

        return mView;
    }

    public class profileRefresh implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!dataSnapshot.exists()){
                /* TODO: realize how stupid this hack is and make something decent */
                try{Thread.sleep(2000);} catch (InterruptedException ignored) {}
                firebase.child("users/" + firebase.getAuth().getUid() + "/public_profile").addListenerForSingleValueEvent(new profileRefresh());
            } else {
                /* fetch account data from server */
                userName = dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("last_name").getValue().toString();
                userDescription = dataSnapshot.child("user_description").getValue().toString();
                isTutor = (Boolean) dataSnapshot.child("is_tutor").getValue();
                tutorDescription = dataSnapshot.child("tutor_description").getValue().toString();

                /* fill in profile from server */
                ((EditText) mView.findViewById(R.id.user_name)).setText(userName);
                ((EditText) mView.findViewById(R.id.user_description)).setText(userDescription);
                ((EditText) mView.findViewById(R.id.tutor_description)).setText(tutorDescription);
                ((CheckBox) mView.findViewById(R.id.is_tutor)).setChecked(isTutor);
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            // TODO: make sure this can be left empty
        }
    }

    public class updateButtonListener implements View.OnClickListener {
        public void onClick(View view) {
            String profilePath = "users/" + firebase.getAuth().getUid() + "/public_profile/";
            //TODO: add private variables

            /* update local variables */
            userName = ((EditText) mView.findViewById(R.id.user_name)).getText().toString().trim();
            if (!userName.contains(" ")){userName = userName + " ";}
            String first_name = userName.substring(0, userName.lastIndexOf(" "));
            String last_name = userName.substring(userName.lastIndexOf(" ") + 1);
            userDescription = ((EditText) mView.findViewById(R.id.user_description)).getText().toString();
            tutorDescription = ((EditText) mView.findViewById(R.id.tutor_description)).getText().toString();
            isTutor = ((CheckBox) mView.findViewById(R.id.is_tutor)).isChecked();

            /* update server */
            firebase.child(profilePath + "first_name").setValue(first_name);
            firebase.child(profilePath + "last_name").setValue(last_name);
            firebase.child(profilePath + "user_description").setValue(userDescription);
            firebase.child(profilePath + "tutor_description").setValue(tutorDescription);
            firebase.child(profilePath + "is_tutor").setValue(isTutor);
        }
    }

    public class refreshButtonListener implements View.OnClickListener {
        public void onClick(View view) {
            firebase.child("users/" + firebase.getAuth().getUid() + "/public_profile").addListenerForSingleValueEvent(new profileRefresh());
        }
    }

    public class checkBoxListener implements CheckBox.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton compoundButton, boolean ticked) {
            if (ticked) {
                mView.findViewById(R.id.tutor_description_heading).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.tutor_description).setVisibility(View.VISIBLE);
            } else {
                mView.findViewById(R.id.tutor_description_heading).setVisibility(View.INVISIBLE);
                mView.findViewById(R.id.tutor_description).setVisibility(View.INVISIBLE);
            }
        }
    }
}