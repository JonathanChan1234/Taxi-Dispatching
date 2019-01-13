package com.jonathan.taxidispatching.activity.passenger_main_activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jonathan.taxidispatching.APIClient.APIClient;
import com.jonathan.taxidispatching.APIInterface.APIInterface;
import com.jonathan.taxidispatching.Event.DriverFoundEvent;
import com.jonathan.taxidispatching.Event.TimerEvent;
import com.jonathan.taxidispatching.Model.Transcation;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.Service.PassengerSocketService;
import com.jonathan.taxidispatching.SharePreference.Session;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionActivity extends AppCompatActivity {
    static FragmentManager manager;
    private APIInterface service = APIClient.getClient().create(APIInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        manager = getSupportFragmentManager();

        //start passenger socket service
        Log.d("Service", "Service started");
        Intent serviceIntent = new Intent(TransactionActivity.this, PassengerSocketService.class);
        startService(serviceIntent);

        //Search Current Active Transaction
        //Case 1: Waiting for driver to pick up
        //Case 2: Driver accept, passenger has to confirm
        //Case 3: Driver and Passenger accept -> wait for meet up
        //Case 4: Finish Ride
        //Case 5: Error

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        service.searchForRecentTranscation(9).enqueue(new Callback<Transcation>() {
            @Override
            public void onResponse(Call<Transcation> call, Response<Transcation> response) {
//                The Passenger has no active transaction
                if (response.code() == 400) {
                    Toast.makeText(TransactionActivity.this, "You have no current transaction", Toast.LENGTH_SHORT).show();
                } else if (response.body() != null) {
                    int transactionid = response.body().data.id;
                    Session.saveCurrentTransaction(TransactionActivity.this, transactionid, response.body().data);
                    switch (response.body().data.status) {
                        case 100:
                            //passenger waiting for available driver
                            changeFragment(Passenger_Waiting_Fragment.newInstance(response.body()), true);
                            break;
                        case 101:
                            //waiting for driver to order and passenger has to accept the order
                            changeFragment(Passenger_Waiting_Fragment.newInstance(response.body()), true);
                            break;
                        case 102:
                            break;

                        case 200:
                            // passenger waits for driver -> show map, location of the driver
                            break;
                        case 201:
                            // passenger meet up with driver and confirm ride
                            // passenger able to track the ride
                            break;
                        case 300:
                            // finish ride
                            break;
                        case 400:
                            // error
                            break;
                        default:
                            // error
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<Transcation> call, Throwable t) {
                Toast.makeText(TransactionActivity.this, "Networking issue", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Driver Found Event
     * go to the next fragment and show the driver information
     * @param event driver found event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDriverFoundEvent(DriverFoundEvent event) {
        Log.d("Transcation", "id: " + event.getTranscation().id);
        Log.d("Driver", "id " + event.getDriver().id);
        // Go to the driver found fragment
        changeFragment(Driver_Found_Fragment.newInstance(event.getTranscation(), event.getDriver()), true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeoutEvent(TimerEvent event) {
        if(event.getMinute() == 0 && event.getSecond() == 0) {
            Log.i("Time out event", "time out");
            //Stop the service
            Intent serviceIntent = new Intent(TransactionActivity.this, PassengerSocketService.class);
            stopService(serviceIntent);
            //Change to the cancel fragment
            changeFragment(CancelFragment.newInstance(), true);
        }
    }

    /**
     * Method to switch fragment
     * @param fragment fragment to switch to
     * @param init whether add to back stack
     */
    public static void changeFragment(Fragment fragment, boolean init) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.passengerActivityContainer, fragment);
        if(!init) transaction.addToBackStack(null);
        manager.popBackStack();
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(TransactionActivity.this, PassengerSocketService.class);
        stopService(serviceIntent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
