package com.jonathan.taxidispatching.activity.passenger_main_activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jonathan.taxidispatching.Event.DriverResponseEvent;
import com.jonathan.taxidispatching.Event.TimerEvent;
import com.jonathan.taxidispatching.Model.Driver;
import com.jonathan.taxidispatching.Model.Transcation;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.activity.DriverMainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Driver_Found_Fragment extends Fragment {
    public static final String TRANSCATION = "transcation";
    public static final String DRIVER = "driver";
    Transcation.Data transcation;
    Driver driver;

    @BindView(R.id.idText)
    TextView idText;
    @BindView(R.id.pickUpPointText)
    TextView pickUpPointText;
    @BindView(R.id.destinationText)
    TextView destinationText;
    @BindView(R.id.requirementText)
    TextView requirementText;
    @BindView(R.id.statusText)
    TextView statusText;
    @BindView(R.id.driverIdText)
    TextView driverIdText;
    @BindView(R.id.driverUsernameText)
    TextView driverUsernameText;
    @BindView(R.id.acceptDriverButton)
    Button acceptDriverButton;
    @BindView(R.id.rejectDriverButton)
    Button rejectDriverButton;
    @BindView(R.id.timerText)
    TextView timerText;

    public Driver_Found_Fragment() {
        // Required empty public constructor
    }


    public static Driver_Found_Fragment newInstance(Transcation.Data transcation, Driver driver) {
        Driver_Found_Fragment fragment = new Driver_Found_Fragment();
        Bundle args = new Bundle();
        args.putSerializable(TRANSCATION, transcation);
        args.putSerializable(DRIVER, driver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Your Order");
        if (getArguments() != null) {
            transcation = (Transcation.Data) getArguments().getSerializable(TRANSCATION);
            driver = (Driver) getArguments().getSerializable(DRIVER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_found, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if(transcation != null && driver != null) {
            idText.setText(String.valueOf(transcation.id));
            pickUpPointText.setText(transcation.startAddr);
            destinationText.setText(transcation.desAddr);
            requirementText.setText(transcation.requirement);
            statusText.setText(String.valueOf(transcation.status));

            driverIdText.setText(String.valueOf(driver.id));
            driverUsernameText.setText(driver.username);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimerEvent event) {
        String text = "Please answer with " + String.format("%02d", event.getMinute()) +
                ":" + String.format("%02d", event.getSecond());
        timerText.setText(text);
    }

    @OnClick(R.id.acceptDriverButton)
    public void acceptDriver() {
        // Fire the accept event to passenger socket service
        EventBus.getDefault().post(new DriverResponseEvent(transcation, driver, 1));
        TransactionActivity.changeFragment(Passenger_driver_connect_fragment.newInstance(), true);
    }

    @OnClick(R.id.rejectDriverButton)
    public void rejectDriver() {
        // Fire the reject event to passenger socket service
        EventBus.getDefault().post(new DriverResponseEvent(transcation, driver, 0));
    }
}
