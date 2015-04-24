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


public class MainActivity extends Activity implements SocratesHome.FragmentSwapListener {

    private String[] mMenuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    public Firebase mFirebase;
    private Profile mProfile;

    private EditText userName;
    private ProfileTracker profileTracker;
    private String tutorDescription;
    private String tutorGreeting;
    private String tutorPrompt;
    private String tutorThank = "Thank you! You are now a Socrates tutor.\n\nIn the next few days, you'll get a notification that the tutees are online.\n\nIf you want to delete your account, just log out.";

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
        Log.d("asdf","1");

        // Drawer Layout Stuff
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
        Log.d("asdf","2");

        // Listener for firebase logins
        mFirebase.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
//                setAuthenticatedUser(authData);
            }
        });
        Log.d("asdf","3");
        // Listener for Facebook logins
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile,Profile currentProfile) {
                if (currentProfile == null) {
                    //TODO: logout method
                } else {
//                    mFirebase.authWithOAuthToken("facebook",AccessToken.getCurrentAccessToken().getToken(),new AuthResultHandler());
                }
            }
        };
        Log.d("asdf","4");
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
    }

    /**
     * drawer mechanics
     */

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    // User selects fragment from left drawer
    private void selectItem(int position) {
//        fragmentSwap(position);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuItems[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * action bar, title, and options
     */

    //TODO: Make Action Bar Decision
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

    /**
     * login to facebook and firebase
     */

    // So far, this does nothing. Everything is done by the listener.
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        public AuthResultHandler(){

        }

        @Override
        public void onAuthenticated(AuthData authData){
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError){}
    }

    public void setAuthenticatedUser(AuthData authData){
        Log.d("Once?", "Twice?");
    }






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

        if (choice == 0) {
            fragment = new SocratesHome();
        } else if (choice == 1) {
            fragment = new TutorList();
        } else if (choice == 2) {
            fragment = new TutorMap();
        } else if (choice == 3) {
            fragment = new MyProfile();
        } else {
            fragment = new SettingsAndAbout();
        }

        Log.d(toString(choice)  )

        String tag = fragment.getTag();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.base_frame, fragment);
        ft.addToBackStack(tag);
        ft.commit();
    }
}