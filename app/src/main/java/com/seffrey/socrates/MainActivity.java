package com.seffrey.socrates;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.provider.Contacts;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;

import java.util.EmptyStackException;


public class MainActivity extends Activity implements SocratesHome.FragmentSwapListener {

    private String[] mMenuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CallbackManager callbackManager;
    private TextView userName;
    private ProfileTracker profileTracker;

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

        FacebookSdk.sdkInitialize(getApplicationContext());

        Firebase.setAndroidContext(this);
        Firebase firebase = new Firebase("https://sizzling-fire-2418.firebaseio.com/");
        firebase.child("success!").setValue("Success!");
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // User selects fragment from left drawer
        FragmentSwap(position);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuItems[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void refreshText(View view){
        TextView userName = (TextView) findViewById(R.id.user_name);

        AccessToken accessToken =  AccessToken.getCurrentAccessToken();
        if (accessToken == null){
            userName.setText("It's still null, dude.");
        }else {
            userName.setText(accessToken.getToken());
        }
    }

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
            @Override
            public void onSuccess(LoginResult loginResult) {
//                userName.setText("BOOOOOM"); TODO: Fix this ungodly mess
//        do stuff
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
        userName = (TextView) findViewById(R.id.user_name);
        Profile profile = Profile.getCurrentProfile();
        if (profile == null) {
            userName.setText("It's null, dude.");
        } else {
            userName.setText("Hi " + profile.getFirstName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void buttonOnePressed(View view){
        FragmentSwap(1);
    }
    public void buttonTwoPressed(View view){
        FragmentSwap(2);
    }
    public void buttonThreePressed(View view){
        FragmentSwap(3);
    }
    public void buttonFourPressed(View view){
        FragmentSwap(4);
    }


    public void FragmentSwap(int choice) {

            Fragment fragment = new TutorList();
        if (choice == 2){
            fragment = new TutorMap();}
        if (choice == 3){
            fragment = new MyProfile();}
        if (choice == 4){
            fragment = new SettingsAndAbout();}

        Profile profile = Profile.getCurrentProfile();
        if (profile == null && choice == 3){setUpLoginButton();}else {

            String tag = fragment.getTag();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.base_frame, fragment);
            ft.addToBackStack(tag);
            ft.commit();

        }
    }
}
