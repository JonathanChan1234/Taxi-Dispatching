package com.jonathan.taxidispatching.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.jonathan.taxidispatching.Event.PassengerFoundEvent;
import com.jonathan.taxidispatching.Model.DriverFoundResponse;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.Utility.DriverNotificationChannel;
import com.jonathan.taxidispatching.activity.DriverMainActivity;
import com.jonathan.taxidispatching.constants.Constants;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class DriverSocketService extends Service
      implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener{
        private static final String TAG = "Socket Service";
        public static final long INTERVAL = 5000;

        private Socket mSocket;
        private boolean isConnected;

        PendingIntent goToActivityIntent;

//    NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        NotificationManager nm;
        NotificationCompat.Builder notificationBuilder_android8;
        Notification.Builder notificationBuilder;

        //Location update parameters
        GoogleApiClient mClient;
        LocationRequest mLocationRequest;
        FusedLocationProviderClient mLocationClient;
        LocationCallback mLocationCallback;

        JSONArray dataPack = new JSONArray();
        String[] mLastUpdateTime = new String[5];
        int dataCount = 0;

        SimpleDateFormat sqlTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public void onCreate() {
            super.onCreate();
            DriverNotificationChannel.createNotificationChannel(this);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            foregroundServiceStart();
            if(isGooglePlayServiceAvailable()) {
                createLocationRequest();
            }
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mClient.connect();
            initHttpSocket();
            startLocationUpdate();
            return START_STICKY; //START_STICKY: resume the service right (without the intent parameter) after it is killed
        }

        private void initHttpSocket() {
            try {
                mSocket = IO.socket("http://192.168.86.183:3000");
                connectSocket();
            } catch(URISyntaxException e) {
                e.printStackTrace();
            }
        }

        private void connectSocket() {
            try {
                mSocket.connect();
                JSONObject object = new JSONObject();
                object.put("identity", "driver");
                object.put("id", 5);
                object.put("objective", "locationUpdate");
                mSocket.emit("join", object);
                isConnected = true;
            } catch(JSONException e) {
                e.printStackTrace();
            }
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeout);
            mSocket.on(Constants.passenger_found_event, passengerFoundEvent);
            mSocket.on("message", onMessageReceived);
        }

        private void disconnectSocket() {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onTimeout);
            mSocket.off("message", onMessageReceived);
            mSocket.off(Constants.passenger_found_event, passengerFoundEvent);
        }

        private Emitter.Listener onConnect = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "connected successfully");
                if(!isConnected) {
                    initHttpSocket();
                }
                isConnected = true;
            }
        };

        private Emitter.Listener onDisconnect = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "socket disconnected");
                notifyUser("server disconnected");
                isConnected = false;
            }
        };

        /**
         * Connection error listener
         * Try to reconnect the server
         */
        private Emitter.Listener onConnectError = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                isConnected = false;
                notifyUser("connection error");
                Log.i(TAG, "socket connection error ");
                stopForeground(true);
                stopSelf();
            }
        };

        private Emitter.Listener onTimeout = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "socket connection timeout ");
            }
        };

        // Passenger Found Event listener
        private Emitter.Listener passengerFoundEvent = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                Log.i("Passenger Found", data.toString());
                notifyUser("You have received a request from a passenger");
                // Convert the JSON to POJO
                Gson gson = new Gson();
                DriverFoundResponse driverFoundResponse = gson.fromJson(data.toString(), DriverFoundResponse.class);
                // Fire the event back to the Driver Main Activity
                EventBus.getDefault().post(new PassengerFoundEvent(driverFoundResponse.transcation, driverFoundResponse.driver));
            }
        };

        private Emitter.Listener onMessageReceived = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                Log.i("Message", data.toString());
                try {
                    String senderNickname = data.getString("user");
                    String message = data.getString("message");
                    notifyUser(message);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        /**
         * Initialize the foreground service (cannot be killed unless manually killed by users)
         */
        private void foregroundServiceStart() {
            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent(this, DriverMainActivity.class);
            notificationIntent.setAction("Action");
            goToActivityIntent = PendingIntent.
                    getActivity(this, 0, notificationIntent, 0);
            if(Build.VERSION.SDK_INT > 26) {
                notificationBuilder_android8 = new NotificationCompat.Builder(this, DriverNotificationChannel.CHANNEL_ID);
                notificationBuilder_android8.setContentTitle("Messenger")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(goToActivityIntent)
                        .setOngoing(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                startForeground(1, notificationBuilder_android8.build());
            } else {
                notificationBuilder = new Notification.Builder(this);
                notificationBuilder.setContentTitle("Messenger")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(goToActivityIntent)
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_HIGH);
                startForeground(1, notificationBuilder.build());
            }
        }

        private void createLocationRequest() {
            //Initialize location request
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(INTERVAL);
            mLocationRequest.setFastestInterval(INTERVAL * 2);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            //Initialize location service client and callback
            mLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.i(TAG, "onLocationResult");
                    if(locationResult == null) {
                        return;
                    }
                    for(Location location : locationResult.getLocations()) {
                        Log.i(TAG, "latitude: " + location.getLatitude() + " longitude: " + location.getLongitude());
                        sendPositionToServer(location);
                    }
                }
            };
        }

        private boolean isGooglePlayServiceAvailable() {
            int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            if(ConnectionResult.SUCCESS == status) {
                return true;
            } else {
                Log.i(TAG, "Google Play Service unavailable");
                return false;
            }
        }

        private void startLocationUpdate() {
            mLocationClient = LocationServices.getFusedLocationProviderClient(this);
            try {
                mLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            } catch(SecurityException e) {
                Log.i(TAG, "Please allow for location update");
                Toast.makeText(getApplicationContext(), "Please allow for location update", Toast.LENGTH_SHORT).show();
            }
        }

        private void sendPositionToServer(Location location) {
            Calendar calendar = Calendar.getInstance();
            mLastUpdateTime[dataCount] = sqlTimeFormat.format(calendar.getTime());
            JSONObject data = new JSONObject();
            try {
                data.put("latitude", location.getLatitude());
                data.put("longitude", location.getLongitude());
                data.put("timestamp", sqlTimeFormat.format(calendar.getTime()));
                dataPack.put(dataCount, data);
            } catch(JSONException e) {
                e.printStackTrace();
            }
            dataCount++;
            if(dataCount == 5) {
                dataCount = 0;
                JSONObject object = new JSONObject();
                try {
                    object.put("id", 5);
                    object.put("data", dataPack);
                    Log.i(TAG, object.toString(1));
                    mSocket.emit("locationUpdate", object);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //Callback when Google API client is connected
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i(TAG, "Google API Connected");
        }

        //Callback when Google API client is disconnected
        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "Google API disconnected");
        }

        //Callback in error
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.i(TAG, "Google API Connection error");
        }

        private void notifyUser(String message) {
            if(Build.VERSION.SDK_INT > 26) {
                notificationBuilder_android8.setContentText(message);
                nm.notify(1, notificationBuilder_android8.build());
            } else {
                notificationBuilder.setContentText(message);
                nm.notify(1, notificationBuilder.build());
            }
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "Service destroyed");
            mLocationClient.removeLocationUpdates(mLocationCallback);
            disconnectSocket();
            stopForeground(true);
            stopSelf();
            super.onDestroy();
        }
}
