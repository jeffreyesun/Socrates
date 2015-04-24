package com.seffrey.socrates;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by Home on 4/24/15.
 */
public class Snippets {

/**

    public void refreshText(View view) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        userName = (EditText) findViewById(R.id.user_name);
        tutorDescription = userName.getText().toString();

        AuthData authData = mFirebase.getAuth();

        if (accessToken != null && tutorDescription != null) {
            mFirebase.child("users/" + authData.getUid() + "/public_profile/tutor_description").setValue(tutorDescription);
        }
        TextView tutorGreetingBox = (TextView) findViewById(R.id.tutor_greeting);
        tutorGreetingBox.setText(tutorThank);
    }


    public void setAuthenticatedUser(AuthData authData) {
        final Firebase pushRef = mFirebase.child("log").push();
        if (authData == null) {
            pushRef.setValue("user was unlogged in.");
            // TODO: consider collapsing this class
        } else {

        }

    public void setUpUser() {
        mProfile = Profile.getCurrentProfile();
        AuthData authData = mFirebase.getAuth();
        Firebase userBase = mFirebase.child("users/" + authData.getUid());

        String first_name = mProfile.getFirstName();
        String last_name = mProfile.getLastName();
        String profile_picture = mProfile.getProfilePictureUri(50, 50).toString();
        //TODO: Work out why this profile picture doesn't work

        userBase.child("public_profile/first_name").setValue(first_name);
        userBase.child("public_profile/last_name").setValue(last_name);
        userBase.child("public_profile/profile_picture").setValue(profile_picture);
        userBase.child("public_profile/is_tutor").setValue(true);
    }

    // Utility class for authentication results
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        public AuthResultHandler() {
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            // TODO: do some error stuff
        }
    }

    }





        mFirebase.child("values").addListenerForSingleValueEvent(new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
        tutorGreeting = dataSnapshot.child("greeting").getValue().toString();
        tutorPrompt = dataSnapshot.child("prompt").getValue().toString();
        tutorThank = dataSnapshot.child("thank").getValue().toString();
        Log.d("JEFFREY", tutorGreeting + tutorPrompt + tutorThank);
        if (tutorGreeting == null) {
        tutorGreeting = "Please connect to the internet and restart the app";
        }
        TextView tutorGreetingBox = (TextView) findViewById(R.id.tutor_greeting);
        tutorGreetingBox.setText(tutorGreeting);

        TextView userNameBox = (TextView) findViewById(R.id.user_name);
        userNameBox.setHint(tutorPrompt);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        mFirebase.child("log").push().setValue(firebaseError.getMessage());
        //do nothing
        }
        });

     */

}
