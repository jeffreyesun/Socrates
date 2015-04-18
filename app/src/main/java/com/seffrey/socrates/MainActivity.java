package com.seffrey.socrates;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.EmptyStackException;


public class MainActivity extends Activity implements SocratesHome.fragmentSwapListener {

    private String[] mMenuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CallbackManager callbackManager;
    private EditText userName;
    private ProfileTracker profileTracker;
    private AuthData mAuthData;
    private Firebase mFirebase;
    private String tutorDescription;

    //TODO: make some stuff private?
    //TODO: update last online and location
    //TODO: global variables userBase, etc.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment socratesHome = new SocratesHome();

        ft.replace(R.id.base_frame, socratesHome);
        ft.commit();

        mMenuItems = getResources().getStringArray(R.array.menu_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Firebase
        Firebase.setAndroidContext(this);
        mFirebase = new Firebase("https://sizzling-fire-2418.firebaseio.com/");
        // login to firebase if already logged in to facebook
        mFirebase.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        });
        //String subjects = "AAPTIS AAS ACABS AERO AEROSP AMCULT ANATOMY ANTHRARC ANTHRBIO ANTHRCUL AOSS APPPHYS ARABAM ARABIC ARCH ARMENIAN ARTDES ASIAN ASIANLAN ASIANPAM ASTRO AUTO BA BCS BE BIOINF BIOLCHEM BIOLOGY BIOMEDE BIOPHYS BIOSTAT CEE CHE CHEM CJS CLARCH CLCIV CMPLXSYS COGSCI COMM COMP COMPLIT CSP CZECH DANCE DUTCH EARTH ECON EDCURINS EDUC EEB EECS EHS ELI ENGLISH ENGR ENS ENSCEN ENVIRON ES ESENG FRENCH GEOG GERMAN GREEK GTBOOKS HEBREW HF HISTART HISTORY HMP HONORS INSTHUM INTLSTD INTMED IOE ITALIAN JAZZ JUDAIC KINESLGY LACS LATIN LATINOAM LHSP LING MACROMOL MATH MATSCIE MCDB MECHENG MEDCHEM MEMS MENAS MFG MICRBIOL MILSCI MKT MODGREEK MOVESCI MUSEUMS MUSICOL MUSMETH MUSTHTRE NATIVEAM NAVARCH NAVSCI NEAREAST NERS NESLANG NEUROSCI NRE NURS ORGSTUDY PAT PATH PERSIAN PHARMACY PHIL PHRMACOL PHYSICS PHYSIOL POLISH POLSCI PORTUG PPE PSYCH PUBHLTH PUBPOL RCARTS RCASL RCCORE RCHUMS RCIDIV RCLANG RCNSCI RCSSCI REEES RELIGION ROMLANG ROMLING RUSSIAN SAC SCAND SEAS SI SLAVIC SOC SPANISH STATS STDABRD STRATEGY SW TCHNCLCM THEORY THTREMUS TO TURKISH UC UKR UP WOMENSTD WRITING YIDDISH";
        //String[] array = subjects.split(" ");

        fragmentSwap(3); //TODO: Get rid of this
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /** drawer mechanics */

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // User selects fragment from left drawer
        fragmentSwap(position);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuItems[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /** action bar, title, and options */


    @Override
    public void setTitle(CharSequence title) {
//        CharSequence mTitle = title;
//        getActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** login to facebook and firebase */

    public void setUpLoginButton() {

        //clear baseFrame
        FrameLayout baseFrame = (FrameLayout) findViewById(R.id.base_frame);
        baseFrame.removeAllViews();

        //stick button in baseFrame
        LayoutInflater inflater = this.getLayoutInflater();
        LinearLayout buttonLayout = (LinearLayout) inflater.inflate(R.layout.facebook_button, null, false);
        baseFrame.addView(buttonLayout);

        //setup Button Callbacks and Permissions
        callbackManager = CallbackManager.Factory.create();

//        profileTracker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(
//                    Profile oldProfile,
//                    Profile currentProfile) {
//                userName.setText(currentProfile.getFirstName());
//            }
//        };

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //on login, go to profile page and initiate firebase login
            @Override
            public void onSuccess(LoginResult loginResult) {
                onFacebookStateChange(loginResult);
            }

            @Override
            public void onCancel() {
                //do nothing
            }

            @Override
            public void onError(FacebookException e) {
                //do nothing
            }
        });

        //Fill in TextBox with info from AccessToken TODO: Get rid of all this shit


//        Profile profile = Profile.getCurrentProfile();
//        if (profile == null) {
//            userName.setText("It's null, dude.");
//        } else {
//            userName.setText("Hi " + profile.getFirstName());
//        }
    }

    public void refreshText(View view){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        userName = (EditText) findViewById(R.id.user_name);
        tutorDescription = userName.getText().toString();
        if (accessToken != null && tutorDescription != null){
            mFirebase.child("users/" + mAuthData.getUid() +"/public_profile/tutor_description").setValue(tutorDescription);
        }
         // TODO: add thank-you
    }

    public void onFacebookStateChange(LoginResult loginResult){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        //if logging in to facebook, log in to firebase, if logging out, log out TODO: put this in facebook accesstoken callback
        if (accessToken != null){
            if (loginResult != null) {
                fragmentSwap(3);
            }
            // if I put this in the other callback, this looks confusing, but it's really just replacing one callback with another
            mFirebase.authWithOAuthToken("facebook", accessToken.getToken(), new AuthResultHandler());
        }else{
            mFirebase.unauth();
            setAuthenticatedUser(null);
        }
    }

    public void setAuthenticatedUser(AuthData authData){
        mAuthData = authData;
        final Firebase pushRef = mFirebase.child("log").push();
        if (authData == null) {
            pushRef.setValue("user was unlogged in.");
            // TODO: consider collapsing this class
        } else {
            pushRef.setValue(authData.getUid() + " was logged in.");
            mFirebase.child("users/" + authData.getUid() + "/public_profile").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        mFirebase.child("log").push().setValue("setUpUser was called");
                        setUpUser();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    mFirebase.child("log").push().setValue(firebaseError.getMessage());
                    //do nothing
                }
            });
        }
    }

    /**
     * Utility class for authentication results
     */
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


    public void setUpUser(){
        Profile profile = Profile.getCurrentProfile();
        Firebase userBase = mFirebase.child("users/" + mAuthData.getUid());

        String first_name = profile.getFirstName();
        String last_name = profile.getLastName();
        String profile_picture = profile.getProfilePictureUri(50,50).toString();

        userBase.child("public_profile/first_name").setValue(first_name);
        userBase.child("public_profile/last_name").setValue(last_name);
        userBase.child("public_profile/profile_picture").setValue(profile_picture);
        userBase.child("public_profile/is_tutor").setValue(true);
    }

    //stupid method to get button presses from homepage
    public void buttonOnePressed(View view){
        fragmentSwap(1);
    }
    public void buttonTwoPressed(View view){
        fragmentSwap(2);
    }
    public void buttonThreePressed(View view){
        fragmentSwap(3);
    }
    public void buttonFourPressed(View view){
        fragmentSwap(4);
    }

    public void fragmentSwap(int choice) {

            Fragment fragment = new TutorList();
        if (choice == 2){
            fragment = new TutorMap();}
        if (choice == 3){
            fragment = new MyProfile();}
        if (choice == 4){
            fragment = new SettingsAndAbout();}

        Profile profile = Profile.getCurrentProfile();
        if ((AccessToken.getCurrentAccessToken() == null || true) && choice == 3){setUpLoginButton(); //TODO: fix this clusterfuck
        }else{

            String tag = fragment.getTag();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.base_frame, fragment);
            ft.addToBackStack(tag);
            ft.commit();

        }
    }
}
