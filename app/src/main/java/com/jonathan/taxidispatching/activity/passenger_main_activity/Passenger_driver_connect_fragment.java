package com.jonathan.taxidispatching.activity.passenger_main_activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jonathan.taxidispatching.R;

public class Passenger_driver_connect_fragment extends Fragment {


    public Passenger_driver_connect_fragment() {
        // Required empty public constructor
    }

    public static Passenger_driver_connect_fragment newInstance() {
        Passenger_driver_connect_fragment fragment = new Passenger_driver_connect_fragment();
        Bundle args = new Bundle();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_passenger_driver_connect, container, false);
    }

}
