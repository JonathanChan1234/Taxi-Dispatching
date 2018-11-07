package com.jonathan.taxidispatching.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.android.volley.Response;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.gms.tasks.OnSuccessListener;
import com.jonathan.taxidispatching.APIClient.APIClient;
import com.jonathan.taxidispatching.APIClient.GoogleMapAPIClient;
import com.jonathan.taxidispatching.APIInterface.APIInterface;
import com.jonathan.taxidispatching.APIInterface.GoogleMapAPIInterface;
import com.jonathan.taxidispatching.APIObject.PlaceResource;
import com.jonathan.taxidispatching.APIObject.Transcation;
import com.jonathan.taxidispatching.AppController.AppController;
import com.jonathan.taxidispatching.AppController.CustomRequest;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.Utility.ErrorMessageUtility;
import com.jonathan.taxidispatching.Utility.GPSPromptEnabled;
import com.jonathan.taxidispatching.Utility.PermissionUtils;
import com.jonathan.taxidispatching.Utility.PlaceUtils;
import com.jonathan.taxidispatching.constants.Constants;
import com.jonathan.taxidispatching.Utility.JSONDirectionParser;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class Main2Activity extends AppCompatActivity implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final int REQUEST_CHECK_SETTINGS = 100;
    //UI Components
    GoogleMap mMap;
    PlaceAutocompleteFragment fromSearchFragment, toSearchFragment; //Search Fragment
    @BindView(R.id.taxiRequestButton)
    Button requestButton;
    @BindView(R.id.timePickView)
    LinearLayout timePickerView;
    @BindView(R.id.timePicker)
    TextView timePickerText;
    @BindView(R.id.clearButton)
    Button clearButton;

    private DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //Constant
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    //Variables
    LatLng[] marker = new LatLng[2]; //store the destination and origin position
    LatLng tempMarker;  //store temp marker position
    String requirement; //store the requirement text of the taxi appointment
    Map<String, String>  address = new HashMap<>();
    private boolean mPermissionDenied = false;

    ProgressDialog loadingDialog;

    //Google Direction and Place API url
    String direction_url = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s";

    //Google API client for location manager
    private GoogleApiClient client;
    GoogleMapAPIInterface googleApiInterface;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///hide the title bar and set the content view
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_main2);
        googleApiInterface = GoogleMapAPIClient.getClient().create(GoogleMapAPIInterface.class);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        //Initialize UI Button
        ButterKnife.bind(this);

        initSearchFragment();
        initMapLoadingDialog();
        initDrawerLayout();

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        client.connect();
        GPSPromptEnabled.promptUserEnabledGPS(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loadingDialog != null) loadingDialog.dismiss();
        if (client.isConnected()) client.disconnect();
    }

    /**
     * Initialize the search fragment for the both origin and destination
     */
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
                address.put("origin", place.getName().toString());
            }
            @Override
            public void onError(Status status) {
                Toast.makeText(Main2Activity.this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        toSearchFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setMarkerInMap(place.getLatLng(), "destination");
                address.put("destination", place.getName().toString());
            }
            @Override
            public void onError(Status status) {
                Toast.makeText(Main2Activity.this, "Error occurred", Toast.LENGTH_SHORT).show();
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

    private void initDrawerLayout() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return true;
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        setSupportActionBar(toolbar);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Initialize the map loading progress dialog
     */
    private void initMapLoadingDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("loading map");
        loadingDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (loadingDialog != null) loadingDialog.dismiss();
        mMap = googleMap;
        enableMyLocation();
        LatLng latlng = new LatLng(22.306398, 114.163554);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        mMap.setOnMapClickListener(clickSetMarker);
    }

    @OnClick(R.id.timePickView)
    public void pickMeetUpTime(View v) {
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timePickerText.setText(hourOfDay + ":" + minute);
                timePickerText.setTextSize(18);
            }
        }, hour, minute, false).show();
    }

    @OnClick(R.id.clearButton)
    public void clearTime(View v) {
        timePickerText.setText("");
        timePickerText.setTextSize(10);
    }

    /**
     * Handle Make Request Button click listener
     * 1. Plot the estimated route
     * 2. Pop up the taxi request dialog
     */
    @OnClick(R.id.taxiRequestButton)
    public void makeTaxiRequest(Button button) {
        if (marker[0] != null && marker[1] != null) {
            String url = String.format(direction_url, marker[0].latitude + "," + marker[0].longitude
                    , marker[1].latitude + "," + marker[1].longitude, Constants.google_map_api_key);
            Log.d("url", url);
            final ProgressDialog getDistanceProgressDialog = new ProgressDialog(Main2Activity.this);
            getDistanceProgressDialog.setTitle("Processing");
            getDistanceProgressDialog.show();
            CustomRequest routeRequest = new CustomRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    getDistanceProgressDialog.dismiss();
                    try {
                        if (response.getString("status").equals("OK")) {
                            plotRoute(response);
                            JSONObject details = PlaceUtils.getRouteInfo(response);
                            //Pop up confirmation dialog
                            if (details != null) confirmationDialogPopUp(details);
                        } else {
                            Toast.makeText(Main2Activity.this, "Route Not Found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, ErrorMessageUtility.getNetworkErrorListener(Main2Activity.this));
            AppController.getInstance().addToRequestQueue(routeRequest, "searchRoute");
        } else {
            Toast.makeText(this, "You have to select both the pick-up point \nand destination", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pop up confirmation dialog
     *
     * @param details contain the JSON Info
     */
    private void confirmationDialogPopUp(JSONObject details) {
        final Dialog dialog = new Dialog(Main2Activity.this);
        dialog.setContentView(R.layout.custom_bill_dialog);
        Button confirmationButton = dialog.findViewById(R.id.confirmationButton);
        TextView detailsText = dialog.findViewById(R.id.billDetailText);
        try {
            String text = "Estimated Distance: " + details.getString("distance") + "\n"
                    + "Estimated Travel Time: " + details.getString("duration") + " minutes";
            if(TextUtils.isEmpty(timePickerText.getText().toString()))text += "\n Meet up time: now";
            else text += "\n Meet up time: " + timePickerText.getText().toString();
            detailsText.setText(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText requirementText = dialog.findViewById(R.id.requirementText);
                String text = requirementText.getText().toString();
                String meet_up_time = timePickerText.getText().toString();
                Call<Transcation> call = apiInterface.startTranscation(1, marker[0].latitude, marker[0].longitude, address.get("origin"),
                        marker[1].latitude, marker[1].longitude, address.get("destination"), meet_up_time, text);
                call.enqueue(new Callback<Transcation>() {
                    @Override
                    public void onResponse(Call<Transcation> call, retrofit2.Response<Transcation> response) {
                        if(response.code() == 200) {
                            Toast.makeText(Main2Activity.this, "Transaction made successfully", Toast.LENGTH_SHORT).show();
                            //Do something
                        } else {
                            Toast.makeText(Main2Activity.this, "Something is wrong. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Transcation> call, Throwable t) {
                        Toast.makeText(Main2Activity.this, "Network connction issue", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
                requirement = text;
            }
        });
        dialog.show();
    }

    /**
     * Handle map click event
     */
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

    /**
     * polyline plot the route between the destination and origin
     *
     * @param result route result
     */
    private void plotRoute(JSONObject result) {
        List<List<HashMap<String, String>>> route = JSONDirectionParser.parse(result);
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

    /**
     * set the marker in the user's selected location
     *
     * @param position the latitude and longitude value of the location
     * @param type     origin/destination
     */
    private void setMarkerInMap(LatLng position, String type) {
        mMap.clear();
        if (type.equals("origin")) {
            marker[0] = position;
        } else if (type.equals("destination")) {
            marker[1] = position;
        }
        MarkerOptions originOptions = new MarkerOptions();
        originOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        originOptions.title("Origin");

        MarkerOptions destinationOptions = new MarkerOptions();
        destinationOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        destinationOptions.title("Destination");

        if (marker[0] != null) {
            originOptions.position(marker[0]);
            mMap.addMarker(originOptions);
        }
        if (marker[1] != null) {
            destinationOptions.position(marker[1]);
            mMap.addMarker(destinationOptions);
        }
    }

    /**
     * find the nearby place name for the users' selected place
     *
     * @param position the latitude and longitude value of the location
     * @param type     origin/destination
     */
    private void findLocation(final LatLng position, final String type) {
        Call<PlaceResource> call = googleApiInterface.getNearbyPlace("zh-TW", position.latitude + "," + position.longitude, "50", Constants.google_map_api_key);
        call.enqueue(new Callback<PlaceResource>() {
            @Override
            public void onResponse(Call<PlaceResource> call, retrofit2.Response<PlaceResource> response) {
                PlaceResource resource = response.body();
                if(resource.status.equals("OK")) {
                    String location = resource.results.get(0).name;
                    setSearchBar(location, type);
                    address.put(type, location);
                } else {
                    setSearchBar(position.latitude + "," + position.longitude, type);
                }
            }
            @Override
            public void onFailure(Call<PlaceResource> call, Throwable t) {
                Toast.makeText(Main2Activity.this, "Network connection issue", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //set the search bar text to the location selected
    private void setSearchBar(String location, String type) {
        if (type.equals("origin")) {
            fromSearchFragment.setText(location);
        } else if (type.equals("destination")) {
            toSearchFragment.setText(location);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    enableMyLocation();
                } else {
                    mPermissionDenied = true;
                }
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                    Log.d("Location", location.getLatitude() + "," + location.getLongitude());
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if(resultCode == Activity.RESULT_OK) {
                    Log.d("onActivityResult", "GPS ON");
                    Toast.makeText(this, "GPS ON", Toast.LENGTH_SHORT).show();
                } else if(resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "Some functions cannot be used", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}