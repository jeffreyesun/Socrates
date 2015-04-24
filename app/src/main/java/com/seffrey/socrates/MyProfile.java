package com.seffrey.socrates;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.AccessToken;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.EmptyStackException;

public class MyProfile extends Fragment {
    private Firebase mFirebase;
//    private SocratesHome.FragmentSwapListener parent;
    private CallbackManager callbackManager;

    public MyProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //set local user variables
        mFirebase = new Firebase("https://sizzling-fire-2418.firebaseio.com/");

        // Set up login button
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                if (accessToken != null){
//                    parent.fragmentSwap(3);
                } else {
                    mFirebase.unauth();
                }
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

        // Check if logged in, select sub-fragment accordingly.
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (Profile.getCurrentProfile() == null) {
            fragmentTransaction.replace(R.id.profile_fragment, new ProfileNotLoggedIn());
        } else {
            fragmentTransaction.replace(R.id.profile_fragment, new ProfileLoggedIn());
        }
        fragmentTransaction.commit();

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        parent = (SocratesHome.FragmentSwapListener) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}