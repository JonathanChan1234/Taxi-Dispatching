package com.jonathan.taxidispatching.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.android.volley.Response;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jonathan.taxidispatching.AppController.AppController;
import com.jonathan.taxidispatching.AppController.CustomRequest;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.Utility.ErrorMessageUtility;
import com.jonathan.taxidispatching.constants.Constants;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Main2Activity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    PlaceAutocompleteFragment fromSearchFragment, toSearchFragment; //Search Fragment
    LatLng[] marker = new LatLng[2]; //store the destination and origin position
    LatLng tempMarker;  //store map click marker position
    String place_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?language=zh-TW&location=%s&radius=%s&key=%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main2);
        initSearchFragment();
    }

    private void initSearchFragment() {
        //find all the fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mainMap);
        fromSearchFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.searchFromText);
        toSearchFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.searchToText);

        fromSearchFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setMarkerInMap(place.getLatLng(), "origin");
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(Main2Activity.this, "Error occured", Toast.LENGTH_SHORT).show();
            }
        });

        toSearchFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setMarkerInMap(place.getLatLng(), "destination");
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(Main2Activity.this, "Error occured", Toast.LENGTH_SHORT).show();
            }
        });
        //define search filter
        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("HK")
                .build();

        //Set hint
        fromSearchFragment.setHint("The taxi will pick you at ...");
        toSearchFragment.setHint("Your Destination is ...");

        //Set filter
        fromSearchFragment.setFilter(filter);
        toSearchFragment.setFilter(filter);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latlng = new LatLng(22.306398, 114.163554);
//        googleMap.addMarker(new MarkerOptions().position(latlng).title("Current position"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        mMap.setOnMapClickListener(clickSetMarker);
    }

    GoogleMap.OnMapClickListener clickSetMarker = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            tempMarker = latLng;
            AlertDialog dialog = new AlertDialog.Builder(Main2Activity.this)
                    .setPositiveButton("Origin", setOrigin)
                    .setNeutralButton("Destination", setDestination)
                    .setTitle("Set as ...")
                    .setCancelable(true)
                    .create();
            dialog.show();
        }
    };

    DialogInterface.OnClickListener setOrigin = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            setMarkerInMap(tempMarker, "origin");
            findLocation(tempMarker, "origin");
        }
    };

    DialogInterface.OnClickListener setDestination = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            setMarkerInMap(tempMarker, "destination");
            findLocation(tempMarker, "destination");
        }
    };

    private void setMarkerInMap(LatLng position, String type) {
        mMap.clear();
        if(type.equals("origin")) {
            marker[0] = position;
        } else if(type.equals("destination")) {
            marker[1] = position;
        }
        MarkerOptions originOptions = new MarkerOptions();
        originOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        originOptions.title("Origin");

        MarkerOptions destinationOptions = new MarkerOptions();
        destinationOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        destinationOptions.title("Destination");

        if(marker[0] != null) {
            originOptions.position(marker[0]);
            mMap.addMarker(originOptions);
        }
        if(marker[1] != null) {
            destinationOptions.position(marker[1]);
            mMap.addMarker(destinationOptions);
        }
    }

    private void findLocation(final LatLng position, final String type) {
        String url = String.format(place_url, position.latitude + "," + position.longitude, "50", Constants.google_map_api_key);
        Log.d("url", url);
        CustomRequest placeSearchRequest = new CustomRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("OK")) {
                        if(response.getJSONArray("results").length() > 0) {
                            String location = response.getJSONArray("results").getJSONObject(0).getString("name");
                            setSearchBar(location, type);
                        }
                    }
                    else {
                        setSearchBar(position.latitude + "," + position.longitude, type);
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, ErrorMessageUtility.getNetworkErrorListener(this));
        AppController.getInstance().addToRequestQueue(placeSearchRequest, "placeSearch");
    }

    private void setSearchBar(String location, String type) {
        if(type.equals("origin")) {
            fromSearchFragment.setText(location);
        } else if(type.equals("destination")) {
            toSearchFragment.setText(location);
        }
    }
}
