package com.seffrey.socrates;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.seffrey.socrates.SocratesHome.FragmentHost;


public class MainActivity extends Activity implements FragmentHost {

    // region Local Variables
    private String[] mMenuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Firebase mFirebase;
    private CallbackManager callbackManager;
    private Thread notifierThread = null;
    private AuthData mAuthData;


    //endregion
    /* TODO: update last online and location */

    // region Activity Lifecycle Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment socratesHome = new SocratesHome();
        ft.replace(R.id.base_frame, socratesHome);
        ft.commit();

        //region Drawer Layout

        mMenuItems = getResources().getStringArray(R.array.menu_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mMenuItems));

        // Drawer click listener callback
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView parent, View view,int position, long id){
                fragmentSwap(position);

                // Highlight the selected item, update the title, and close the drawer
                mDrawerList.setItemChecked(position, true);
                setTitle(mMenuItems[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        //endregion

        /* Facebook */
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        /* Firebase */
        Firebase.setAndroidContext(this);
        mFirebase = new Firebase("https://sizzling-fire-2418.firebaseio.com/");

        /* Listener for Firebase logins */
        mFirebase.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        });

        /* Listener for Facebook logins */
        new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                fragmentSwap(3);
                if (currentProfile == null) {
                    mFirebase.unauth();
                } else {
                    mFirebase.authWithOAuthToken("facebook",AccessToken.getCurrentAccessToken().getToken(), null);
                }
            }
        };
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
        Log.d("Callback", "Called");
    }

    //endregion

    // region Action Bar TODO: Make Action Bar Decision

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

    // endregion

    // region Facebook

    public CallbackManager getCallbackManager(){
        return callbackManager;
    }

    //endregion

    // region Firebase

    /* either stops the notifier (on logout), calls setUpUser (for new user), or calls summonNotifierDaemon (else) */
    public void setAuthenticatedUser(AuthData authData) {
        if (authData == null) {
            try{notifierThread.interrupt();Log.d("NotifierDaemon", "interrupted by setAuthenticatedUser");} catch (NullPointerException e){Log.d("notifierDaemon", "NullPointerException");} // interrupts the tracker thread
        } else {
            mFirebase.child("users/" + authData.getUid() + "/public_profile").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        setUpUser();
                    } else {
                        summonNotifierDaemon();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });

        }
    }

    public class notifierDaemon implements Runnable{

        @Override
        public void run() {
            Log.d("notifierDaemon", "summoned");
            while (true){
                // Every 30s, updates location in unix time
                try{Thread.sleep(2000);} catch (InterruptedException ignored) {}
                int lastOnline = (int) (System.currentTimeMillis()/1000L);
                try {mFirebase.child("tutors/" + mFirebase.getAuth().getUid()).setValue(lastOnline);} catch (NullPointerException ignored) {}
                try{Thread.sleep(28000);} catch (InterruptedException e) {Log.d("notifierDaemon","interrupted");return;}
            }
        }
    }

    /* While user is a tutor, track on server */
    public void summonNotifierDaemon(){
        mAuthData = mFirebase.getAuth();
        mFirebase.child("users/" + mAuthData.getUid() + "/public_profile/is_tutor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isTutor = (Boolean) dataSnapshot.getValue();
                if (isTutor == null){isTutor = false;}
                if (isTutor) {
                    notifierThread = new Thread(new notifierDaemon());
                    notifierThread.start();
                } else {
                    try{notifierThread.interrupt();Log.d("notifierDaemon", "interrupted by Summoner");} catch (NullPointerException e){Log.d("notifierDaemon", "NullPointerException");} // interrupts the tracker thread
                    mFirebase.child("tutors/"+mAuthData.getUid()).setValue(null); // takes user off tutor list
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setUpUser(){
        Firebase userBase = mFirebase.child("users/" + mFirebase.getAuth().getUid() + "/public_profile");
        Profile profile = Profile.getCurrentProfile();
        userBase.child("first_name").setValue(profile.getFirstName());
        userBase.child("last_name").setValue(profile.getLastName());
        userBase.child("is_tutor").setValue(false);
        userBase.child("user_description").setValue("Please enter a description of yourself. For example: \"I am a sophomore in engineering looking for help with IOE.\"");
        userBase.child("tutor_description").setValue("Please enter a description of what subjects you can tutor. For example: \"I speak French fluently, and I can also help with Orgo!\"");
        summonNotifierDaemon();
    }

    //endregion

    //region Fragment Navigation

    // Get button presses from homepage
    public void buttonOnePressed(View view) {
        fragmentSwap(1);
    }

    public void buttonTwoPressed(View view) {
        fragmentSwap(2);
    }

    public void buttonThreePressed(View view) {
        fragmentSwap(3);
    }

    public void buttonFourPressed(View view) {
        fragmentSwap(4);
    }

    public void fragmentSwap(int choice) {
        Fragment fragment;
        switch (choice) {
            case 0:
                fragment = new SocratesHome();
                break;
            case 1:
                fragment = new TutorList();
                break;
            case 2:
                fragment = new TutorMap();
                break;
            case 3:
                fragment = new MyProfile();
                break;
            default:
                fragment = new SettingsAndAbout();
                break;
        }

        Log.d(Integer.toString(choice), "5");


        String tag = fragment.getTag();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.base_frame, fragment);
        ft.addToBackStack(tag);
        ft.commit();

    }

    //endregion
}