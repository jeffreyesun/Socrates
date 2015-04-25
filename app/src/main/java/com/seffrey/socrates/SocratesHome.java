package com.seffrey.socrates;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.seffrey.socrates.SocratesHome.FragmentHost} interface
 * to handle interaction events.
 */
public class SocratesHome extends Fragment {

    private FragmentHost mListener;

    public SocratesHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_socrates_home, container, false);
    }

    public void onButtonPressed(int choice) {
        if (mListener != null) {
            mListener.fragmentSwap(choice);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentHost) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentHost");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface FragmentHost {
        public CallbackManager getCallbackManager();
        public void fragmentSwap(int choice);
    }

}