package com.jonathan.taxidispatching.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jonathan.taxidispatching.AppController.AppController;
import com.jonathan.taxidispatching.AppController.CustomRequest;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.Utility.ErrorMessageUtility;
import com.jonathan.taxidispatching.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    EditText usernameText, passwordText, verificationText, phoneText;
    Button sendSMSButton, registerButton;
    TextView counterText;

    String phoneNumber = "";
    Handler counterHandler;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_account);
        initUI();
        counterHandler = new CountingHandler(this);
    }

    private void initUI() {
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        phoneText = findViewById(R.id.phoneText);
        verificationText = findViewById(R.id.verificationCodeText);
        sendSMSButton = findViewById(R.id.sendSMSButton);
        counterText = findViewById(R.id.counterText);
        registerButton = findViewById(R.id.registerButton);

        sendSMSButton.setOnClickListener(sendSMSListener);
        registerButton.setOnClickListener(registerAccount);
    }
    //Send SMS Button Listener
    View.OnClickListener sendSMSListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            checkEmpty();
        }
    };

    //Check whether all the info is filled
    private void checkEmpty() {
        if(TextUtils.isEmpty(usernameText.getText().toString()) ||
                TextUtils.isEmpty(passwordText.getText().toString()) ||
                TextUtils.isEmpty(phoneText.getText().toString())) {
            Toast.makeText(CreateAccountActivity.this, "all the info have to be filled", Toast.LENGTH_LONG).show();
        } else {
            checkAccountValidity();
        }
    }

    // Check account validity
    private void checkAccountValidity() {
        Map<String, String> params = new HashMap<>();
        params.put("username", usernameText.getText().toString());
        params.put("password", passwordText.getText().toString());
        params.put("phonenumber", phoneText.getText().toString());
        phoneNumber = phoneText.getText().toString();
        CustomRequest request = new CustomRequest(Request.Method.POST, Constants.verification_api, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("success") == 1) {
                        Toast.makeText(CreateAccountActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        verificationText.setVisibility(View.VISIBLE);
                        counterText.setVisibility(View.VISIBLE);
                        registerButton.setVisibility(View.VISIBLE);
                        sendSMSButton.setText("Send again");
                        startCounting();
                    } else {
                        Toast.makeText(CreateAccountActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, ErrorMessageUtility.getNetworkErrorListener(CreateAccountActivity.this));
        AppController.getInstance().addToRequestQueue(request, "verification");
    }

    //counting started (reminder user to input code)
    private void startCounting() {
        Thread countingThread = new Thread() {
            @Override
            public void run() {
                int i = 60;
                flag = true;
                while(flag) {
                    SystemClock.sleep(1000);
                    Message msg = Message.obtain();
                    msg.what = 0;
                    msg.obj = i + "";
                    counterHandler.sendMessage(msg);
                    i--;
                    if(i < 0) {
                        flag = false;
                        break;
                    }
                }
            }
        };
        countingThread.start();
    }


    View.OnClickListener registerAccount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Map<String, String> params = new HashMap<>();
            params.put("username", usernameText.getText().toString());
            params.put("password", passwordText.getText().toString());
            params.put("phonenumber", phoneText.getText().toString());
            params.put("code", verificationText.getText().toString());
            CustomRequest registerRequest = new CustomRequest(Request.Method.POST, Constants.create_account_api, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getInt("success") == 1) {
                            Toast.makeText(CreateAccountActivity.this, "Create Account successfully", Toast.LENGTH_LONG).show();
                            //jump to the main activity
                            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(CreateAccountActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, ErrorMessageUtility.getNetworkErrorListener(CreateAccountActivity.this));
            AppController.getInstance().addToRequestQueue(registerRequest, "registerRequest");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //reset the counter
        if(flag) {
            flag = false;
            counterText.setVisibility(View.GONE);
            verificationText.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
        }
    }

    static class CountingHandler extends Handler {
        WeakReference<CreateAccountActivity> reference;
        CountingHandler(CreateAccountActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CreateAccountActivity activity = reference.get();
            switch(msg.what){
                case 0:
                    Log.d("Count", msg.obj.toString());
                    String countingMessage = "Enter the code within " + msg.obj.toString() + " seconds";
                    activity.counterText.setText(countingMessage);
                    break;
                default:
                    break;
            }
        }
    }
}
