package com.jonathan.taxidispatching.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import com.jonathan.taxidispatching.AppController.AppController;
import com.jonathan.taxidispatching.AppController.CustomRequest;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.Utility.ErrorMessageUtility;
import com.jonathan.taxidispatching.Utility.MapLayout;
import com.jonathan.taxidispatching.constants.Constants;
import com.jonathan.taxidispatching.Utility.JSONDirectionParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private MapLayout mapsLayout;
    private GoogleMap mMap;

    private EditText fromEdit;
    private EditText toEdit;
    private Button requestButton, directionRequestButton;

    public String direction_url = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s";
    ArrayList markerPoint = new ArrayList();
    LatLng[] marker = new LatLng[2];

    SupportPlaceAutocompleteFragment searchFragment;
    String currentFocusField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        findView();
    }

    private void initUI() {
        mapsLayout = new MapLayout(this);
        setContentView(mapsLayout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        searchFragment  = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void findView() {
        fromEdit = mapsLayout.fromText;
        toEdit = mapsLayout.toText;
        requestButton = mapsLayout.requestButton;
        directionRequestButton = mapsLayout.directionRequestButton;
        requestButton.setOnClickListener(makeRequest);
        currentFocusField = "from";
        fromEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                currentFocusField = "from";
                return false;
            }

        });
        toEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                currentFocusField = "to";
                return false;
            }
        });
        directionRequestButton.setOnClickListener(setRoute);
    }

    View.OnClickListener makeRequest = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String origin = fromEdit.getText().toString().trim();
            String destination = toEdit.getText().toString().trim();
            String url = String.format(direction_url, origin, destination, Constants.google_map_api_key);
            final ProgressDialog dialog = new ProgressDialog(MapsActivity.this);
            dialog.setTitle("Processing");
            dialog.show();
            CustomRequest routeRequest = new CustomRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    try{
                        if(response.getString("status") == "OK") {
                            plotRoute(response);
                        } else {
                            Toast.makeText(MapsActivity.this, "Route Not Found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, ErrorMessageUtility.getNetworkErrorListener(MapsActivity.this));
            AppController.getInstance().addToRequestQueue(routeRequest, "searchRoute");
        }
    };

    /**    Plot the route between the origin and destination
     *
     * @param result the route result retrieved from the direction API
     */

   private void plotRoute(JSONObject result) {
       List<List<HashMap<String,String >>> route = JSONDirectionParser.parse(result);
       ArrayList points;
       PolylineOptions lineOptions = null;
       MarkerOptions markerOptions = new MarkerOptions();

       for (int i = 0; i < route.size(); i++) {
           points = new ArrayList();
           lineOptions = new PolylineOptions();

           List<HashMap<String, String>> path = route.get(i);

           for (int j = 0; j < path.size(); j++) {
               HashMap<String, String> point = path.get(j);

               double lat = Double.parseDouble(point.get("lat"));
               double lng = Double.parseDouble(point.get("lng"));
               LatLng position = new LatLng(lat, lng);

               points.add(position);
           }

           lineOptions.addAll(points);
           lineOptions.width(12);
           lineOptions.color(Color.RED);
           lineOptions.geodesic(true);
       }
       mMap.addPolyline(lineOptions);
   }

   View.OnClickListener setRoute = new View.OnClickListener() {
       @Override
       public void onClick(View view) {

       }
   };
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latlng = new LatLng(22.306398, 114.163554);
        googleMap.addMarker(new MarkerOptions().position(latlng).title("Current position"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        mMap.setOnMapClickListener(mapListener);
    }

    /**
    Map Click Listener handle the map click
    select the origin and destination
    **/
    GoogleMap.OnMapClickListener mapListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            mMap.clear();
            Log.d("marker", currentFocusField);
            if(currentFocusField.equals("from")) {
                marker[0] = latLng;
                Log.d("marker", "set from");
            } else if(currentFocusField.equals("to")) {
                marker[1] = latLng;
                Log.d("marker", "set to");
            }
            MarkerOptions originOptions = new MarkerOptions();
            originOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            MarkerOptions destinationOptions = new MarkerOptions();
            destinationOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            if(marker[0] != null) {
                originOptions.position(marker[0]);
                mMap.addMarker(originOptions);
            }
            if(marker[1] != null) {
                destinationOptions.position(marker[1]);
                mMap.addMarker(destinationOptions);
            }
        }
    };
}
