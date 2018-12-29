package com.jonathan.taxidispatching.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.activity.ui.driver_main.DriverMainFragment;

public class DriverMainActivity extends AppCompatActivity implements
        DriverMainFragment.DataExchangeInterface {
    static FragmentManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main_activity);
        manager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.driverActivityContainer, DriverMainFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void toWaitingFragment() {
        changeFragment(WaitingFragment.newInstance(), true);
    }

    private static void changeFragment(Fragment fragment, boolean init) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.driverActivityContainer, fragment);
        if(!init) transaction.addToBackStack(null);
        manager.popBackStack();
        transaction.commit();
    }

    public void onFragmentInteraction(Uri uri) {

    }

    public void sendData(Bundle message) {

    }
}
