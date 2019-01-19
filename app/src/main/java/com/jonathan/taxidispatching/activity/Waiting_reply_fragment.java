package com.jonathan.taxidispatching.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jonathan.taxidispatching.R;


public class Waiting_reply_fragment extends Fragment {


    public Waiting_reply_fragment() {
        // Required empty public constructor
    }

    public static Waiting_reply_fragment newInstance() {
        Waiting_reply_fragment fragment = new Waiting_reply_fragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reply_waiting, container, false);
    }

}
