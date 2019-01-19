package com.jonathan.taxidispatching.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jonathan.taxidispatching.Event.PassengerFoundEvent;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.activity.ui.driver_main.DriverMainFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DriverMainActivity extends AppCompatActivity {
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

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public static void toWaitingFragment() {
        changeFragment(WaitingFragment.newInstance(), true);
    }

    public static void toReplyWaitingFragment() {
        changeFragment(Waiting_reply_fragment.newInstance(), true);
    }

    private static void changeFragment(Fragment fragment, boolean init) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.driverActivityContainer, fragment);
        if(!init) transaction.addToBackStack(null);
        manager.popBackStack();
        transaction.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPassengerFoundEvent(PassengerFoundEvent event) {
        changeFragment(PassengerFoundFragment.newInstance(event.getTranscation(), event.getDriver(), event.getData()), true);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
