package com.jonathan.taxidispatching.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.jonathan.taxidispatching.APIClient.APIClient;
import com.jonathan.taxidispatching.APIInterface.APIInterface;
import com.jonathan.taxidispatching.APIObject.AccountResponse;
import com.jonathan.taxidispatching.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SignInActivity extends AppCompatActivity {
    @BindView(R.id.phoneTextSignIn) EditText phoneText;
    @BindView(R.id.passwordTextSignIn) EditText passwordText;
    @BindView(R.id.signInButton) Button signInButton;
    @BindView(R.id.passengerButtonInSignIn) RadioButton passengerButton;

    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_in);

        //Bind UI Components
        ButterKnife.bind(this);
        // create API interface for networking
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    //Check whether all the info is filled
    @OnClick(R.id.signInButton)
    public void signIn() {
        if(TextUtils.isEmpty(passwordText.getText().toString()) ||
                TextUtils.isEmpty(phoneText.getText().toString())) {
            Toast.makeText(SignInActivity.this, "all the info have to be filled", Toast.LENGTH_LONG).show();
        } else {
            if(passengerButton.isChecked()) {
                makeRequest("passenger");
            } else {
                makeRequest("driver");
            }
        }
    }

    public void makeRequest(String identity) {
        if(identity.equals("passenger")) {
            apiInterface.passengerSignIn(phoneText.getText().toString(), passwordText.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<AccountResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(AccountResponse accountResponse) {
                            if (accountResponse.success == 1) {
                                Toast.makeText(SignInActivity.this, "Successfully Log in", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignInActivity.this, "Wrong password", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(SignInActivity.this, "Network Issue", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            apiInterface.driverSignIn(phoneText.getText().toString(), passwordText.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<AccountResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(AccountResponse accountResponse) {
                            if (accountResponse.success == 1) {
                                Toast.makeText(SignInActivity.this, "Successfully Log in", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignInActivity.this, "Wrong password", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(SignInActivity.this, "Network Issue", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
