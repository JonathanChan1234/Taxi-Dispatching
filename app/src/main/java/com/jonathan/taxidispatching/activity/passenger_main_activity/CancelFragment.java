package com.jonathan.taxidispatching.activity.passenger_main_activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jonathan.taxidispatching.R;


public class CancelFragment extends Fragment {


    public CancelFragment() {
        // Required empty public constructor
    }

    public static CancelFragment newInstance() {
        CancelFragment cancelFragment = new CancelFragment();
        return  cancelFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cancel, container, false);
    }

}
