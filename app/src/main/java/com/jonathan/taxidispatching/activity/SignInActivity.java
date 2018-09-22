package com.jonathan.taxidispatching.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    EditText phoneText, passwordText;
    Button signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_in);
        initUI();
    }

    private void initUI() {
        phoneText = findViewById(R.id.phoneTextSignIn);
        passwordText = findViewById(R.id.passwordTextSignIn);
        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(signInListener);
    }

    View.OnClickListener signInListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            signIn();
        }
    };

    //Check whether all the info is filled
    private void signIn() {
        if(TextUtils.isEmpty(passwordText.getText().toString()) ||
                TextUtils.isEmpty(phoneText.getText().toString())) {
            Toast.makeText(SignInActivity.this, "all the info have to be filled", Toast.LENGTH_LONG).show();
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("password", passwordText.getText().toString());
            params.put("phonenumber", phoneText.getText().toString());
            CustomRequest request = new CustomRequest(Request.Method.POST, Constants.signIn_api, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getInt("success") == 1) {
                            Toast.makeText(SignInActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            //jump to the main activity
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignInActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, ErrorMessageUtility.getNetworkErrorListener(SignInActivity.this));
            AppController.getInstance().addToRequestQueue(request, "signIn");
        }
    }
}
