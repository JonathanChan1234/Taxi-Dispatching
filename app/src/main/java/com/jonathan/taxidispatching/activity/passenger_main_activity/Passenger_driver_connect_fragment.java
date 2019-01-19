package com.jonathan.taxidispatching.activity.passenger_main_activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jonathan.taxidispatching.Event.LocationUpdateEvent;
import com.jonathan.taxidispatching.Model.Driver;
import com.jonathan.taxidispatching.Model.Transcation;
import com.jonathan.taxidispatching.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Passenger_driver_connect_fragment extends Fragment implements OnMapReadyCallback {
    public static final String TRANSCATION = "transcation";
    public static final String DRIVER = "driver";
    GoogleMap map;

    @BindView(R.id.startRideButton)
    Button startRideButton;
    @BindView(R.id.cancelRideButton)
    Button cancelRideButton;
    @BindView(R.id.messageDriverButton)
    Button messageDriverButton;
    @BindView(R.id.callDriverButton)
    Button callDriverButton;

    Transcation.Data transcation;
    Driver driver;

    public Passenger_driver_connect_fragment() {
        // Required empty public constructor
    }

    public static Passenger_driver_connect_fragment newInstance(Transcation.Data transcation, Driver driver) {
        Passenger_driver_connect_fragment fragment = new Passenger_driver_connect_fragment();
        Bundle args = new Bundle();
        args.putSerializable(TRANSCATION, transcation);
        args.putSerializable(DRIVER, driver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transcation = (Transcation.Data) getArguments().getSerializable(TRANSCATION);
            driver = (Driver) getArguments().getSerializable(DRIVER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_driver_connect, container, false);
        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.driverMap);
        if(fragment != null) {
            fragment.getMapAsync(this);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMarker();
    }

    private void setMarker() {
        if(transcation != null) {
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

            LatLng latlng = new LatLng(Double.parseDouble(transcation.startLat), Double.parseDouble(transcation.startLong));
            Log.i("Passenger Found", latlng.latitude + ", " + latlng.longitude);
            map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            map.moveCamera(CameraUpdateFactory.zoomTo(16));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationUpdateEvent(LocationUpdateEvent event) {
        Log.i("location update event", "received");
        map.clear();
        setMarker();
        MarkerOptions locationOptions = new MarkerOptions();
        locationOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle));
        LatLng latLng = new LatLng(Double.parseDouble(event.getLocation().location.latitude),
                Double.parseDouble(event.getLocation().location.longitude));
        locationOptions.position(latLng);
        map.addMarker(locationOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.moveCamera(CameraUpdateFactory.zoomTo(16));
    }
}
