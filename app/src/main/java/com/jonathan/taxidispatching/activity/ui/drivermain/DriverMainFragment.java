package com.jonathan.taxidispatching.activity.ui.drivermain;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jonathan.taxidispatching.activity.R;

public class DriverMainFragment extends Fragment {

    private DriverMainViewModel mViewModel;

    public static DriverMainFragment newInstance() {
        return new DriverMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.driver_main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DriverMainViewModel.class);
        // TODO: Use the ViewModel
    }

}
