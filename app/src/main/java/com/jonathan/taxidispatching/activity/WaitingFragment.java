package com.jonathan.taxidispatching.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.google.android.gms.location.LocationSettingsStates;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.Service.DriverSocketService;
import com.jonathan.taxidispatching.Utility.GPSPromptEnabled;
import com.jonathan.taxidispatching.activity.ui.driver_main.DriverMainDataModel;

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
    DriverMainDataModel dataModel;
    ProgressDialog progressDialog;

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
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Processing");
        progressBar.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        dataModel = new DriverMainDataModel();
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
                        Toast.makeText(activity, "You have to turn on the GPS in order to use the service", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void startSearchingForPassenger() {
        progressDialog.show();
        dataModel.setOccupied(4, 0, new DriverMainDataModel.onDataSucessCallBack() {
            @Override
            public void onCallBack(int success) {
                if(success == 1) {
                    searchButton.setText("On Serve");
                    progressDialog.dismiss();
                    Intent intent = new Intent(activity, DriverSocketService.class);
                    activity.startService(intent);
                    enableCancel();
                } else {
                    Toast.makeText(activity, "Something is wrong, please check your internet connection",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void enableCancel() {
        progressBar.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        searchButton.setEnabled(false);
    }

    @OnClick(R.id.cancelSearchButton)
    public void cancelSearch() {
        progressDialog.show();
        dataModel.setOccupied(4, 1, new DriverMainDataModel.onDataSucessCallBack() {
            @Override
            public void onCallBack(int success) {
                if(success == 1) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(activity, DriverSocketService.class);
                    activity.stopService(intent);

                    //Update the status of the driver by HTTP
                    progressBar.setVisibility(View.INVISIBLE);
                    cancelButton.setVisibility(View.INVISIBLE);
                    searchButton.setEnabled(true);
                } else {
                    Toast.makeText(activity, "Something is wrong, please check your internet connection",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
