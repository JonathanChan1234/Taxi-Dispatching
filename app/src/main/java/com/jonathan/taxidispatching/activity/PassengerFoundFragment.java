package com.jonathan.taxidispatching.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jonathan.taxidispatching.Event.PassengerFoundResponse;
import com.jonathan.taxidispatching.Event.TimerEvent;
import com.jonathan.taxidispatching.Model.Driver;
import com.jonathan.taxidispatching.Model.Transcation;
import com.jonathan.taxidispatching.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PassengerFoundFragment extends Fragment implements OnMapReadyCallback {
    public static final String TRANSCATION = "transcation";
    public static final String DRIVER = "driver";
    public static final String DATA = "data";
    boolean isClicked = false;
    GoogleMap map;

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

    @BindView(R.id.acceptDealButton)
    Button acceptButton;
    @BindView(R.id.rejectDealButton)
    Button rejectButton;
    @BindView(R.id.timeCounterText)
    TextView timerCounterText;
    @BindView(R.id.toDestinationButton)
    Button toDestinationButton;

    Transcation.Data transcation;
    Driver driver;
    String data;

    public PassengerFoundFragment() {
        // Required empty public constructor
    }

    public static PassengerFoundFragment newInstance(Transcation.Data transcation, Driver driver, String data) {
        PassengerFoundFragment fragment = new PassengerFoundFragment();
        Bundle args = new Bundle();
        args.putSerializable(TRANSCATION, transcation);
        args.putSerializable(DRIVER, driver);
        args.putString(DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transcation = (Transcation.Data) getArguments().getSerializable(TRANSCATION);
            driver = (Driver) getArguments().getSerializable(DRIVER);
            data = getArguments().getString(DATA);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (transcation != null && driver != null) {
            idText.setText(String.valueOf(transcation.id));
            pickUpPointText.setText(transcation.startAddr);
            destinationText.setText(transcation.desAddr);
            requirementText.setText(transcation.requirement);
            statusText.setText(String.valueOf(transcation.status));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_found, container, false);
        ButterKnife.bind(this, view);
        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.routeMap);
        if(fragment != null) {
            Log.d("OnMapReady", "fragment exist");
            fragment.getMapAsync(this);
        } else {
            Log.d("OnMapReady", "no map fragment found");
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Log.d("Map ready", "ready");
        LatLng latlng = new LatLng(Double.parseDouble(transcation.startLat), Double.parseDouble(transcation.startLong));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        placeMarker();
    }

    private void placeMarker() {
        MarkerOptions originOptions = new MarkerOptions();
        originOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        originOptions.title("Pick-up");

        MarkerOptions destinationOptions = new MarkerOptions();
        destinationOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        destinationOptions.title("Destination");

        originOptions.position(new LatLng(Double.parseDouble(transcation.startLat), Double.parseDouble(transcation.startLong)));
        map.addMarker(originOptions);
        destinationOptions.position(new LatLng(Double.parseDouble(transcation.desLat), Double.parseDouble(transcation.desLong)));
        map.addMarker(destinationOptions);
    }

    @OnClick(R.id.toDestinationButton)
    public void toDestination() {
        if(map != null) {
            if (isClicked) {
                LatLng latlng = new LatLng(Double.parseDouble(transcation.startLat), Double.parseDouble(transcation.startLong));
                Log.i("Passenger Found", latlng.latitude + ", " + latlng.longitude);
                map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                map.moveCamera(CameraUpdateFactory.zoomTo(14));
                toDestinationButton.setText("To Destination");
                isClicked = false;
            } else {
                isClicked  = true;
                Log.i("Destination", "{" + transcation.startLat + ", " + transcation.startLong + "}");
                LatLng latlng = new LatLng(Double.parseDouble(transcation.desLat), Double.parseDouble(transcation.desLong));
                map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                map.moveCamera(CameraUpdateFactory.zoomTo(14));
                toDestinationButton.setText("To Pick up point");
            }
        }
    }

    @OnClick(R.id.acceptDealButton)
    public void acceptDeal() {
        EventBus.getDefault().post(new PassengerFoundResponse(transcation, driver, data, 1));
        DriverMainActivity.toReplyWaitingFragment();
    }

    @OnClick(R.id.rejectDealButton)
    public void rejectDeal() {
        EventBus.getDefault().post(new PassengerFoundResponse(transcation, driver, data, 0));
        DriverMainActivity.toWaitingFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimerEvent event) {
        String text = "Please answer with " + String.format("%02d", event.getMinute()) +
                ":" + String.format("%02d", event.getSecond());
        timerCounterText.setText(text);
        if(event.getMinute() == 0 && event.getSecond() == 0) {
            DriverMainActivity.toWaitingFragment();
        }
    }
}
