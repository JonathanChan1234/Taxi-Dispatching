package com.jonathan.taxidispatching.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.jonathan.taxidispatching.R;

public class LogInActivity extends AppCompatActivity {
    Button createAccountButton, signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_log_in);
        initUI();
    }

    private void initUI() {
        createAccountButton = findViewById(R.id.jumpToRegisterButton);
        createAccountButton.setOnClickListener(jumpToCreateAccount);
        signInButton = findViewById(R.id.jumpToSignInButton);
        signInButton.setOnClickListener(jumpToSignIn);
    }

    View.OnClickListener jumpToCreateAccount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LogInActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener jumpToSignIn =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LogInActivity.this,SignInActivity.class);
            startActivity(intent);
        }
    };
}
