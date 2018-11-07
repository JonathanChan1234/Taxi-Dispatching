package com.jonathan.taxidispatching.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.activity.ui.drivermain.DriverMainFragment;

public class DriverMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DriverMainFragment.newInstance())
                    .commitNow();
        }
    }
}
