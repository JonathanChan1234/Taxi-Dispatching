package com.jonathan.taxidispatching.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.location.LocationSettingsStates;
import com.jonathan.taxidispatching.Event.PassengerFoundEvent;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.Utility.GPSPromptEnabled;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jonathan.taxidispatching.constants.Constants.REQUEST_CHECK_SETTINGS;


public class WaitingFragment extends Fragment {
    @BindView(R.id.startSearchingPassengerButton)
    Button searchButton;
    @BindView(R.id.waitingProgressBar)
    ProgressBar progressBar;
    @BindView(R.id.cancelSearchButton)
    Button cancelButton;

    Activity activity;

    public static WaitingFragment newInstance() {
        return new WaitingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waiting, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        progressBar.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        GPSPromptEnabled.promptUserEnabledGPS(activity);
    }

    @OnClick(R.id.startSearchingPassengerButton)
    public void showWarningDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setTitle("Reminder")
                .setMessage("Your location will be reported to remote server for searching passengers nearby")
                .setPositiveButton("AGREE", checkPermissionForLocation)
                .setNegativeButton("NOT AGREE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        dialog.show();
    }

    /**
     * Check whether the user has turn on GPS
     */
    DialogInterface.OnClickListener checkPermissionForLocation = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            startSearchingForPassenger();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void observeChange(PassengerFoundEvent event) {

    }

    public void startSearchingForPassenger() {

    }
}
