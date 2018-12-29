package com.jonathan.taxidispatching.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jonathan.taxidispatching.APIClient.APIClient;
import com.jonathan.taxidispatching.APIInterface.APIInterface;
import com.jonathan.taxidispatching.Model.AccountResponse;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.SharePreference.Session;
import com.jonathan.taxidispatching.Utility.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class CreateAccountActivity extends AppCompatActivity {
    //onActivityResult Parameters
    public static final int REQUEST_CAMERA = 100;
    public static final int SELECT_FILE = 101;
    private static final String TAG = "RxJava";

    //Initialize the UI components
    @BindView(R.id.usernameText) EditText usernameText;
    @BindView(R.id.passwordText) EditText passwordText;
    @BindView(R.id.verificationCodeText) EditText verificationText;
    @BindView(R.id.phoneText) EditText phoneText;
    @BindView(R.id.sendSMSButton) Button sendSMSButton;
    @BindView(R.id.registerButton) Button registerButton;
    @BindView(R.id.counterText) TextView counterText;
    @BindView(R.id.profileImg) ImageView profileImg;
    @BindView(R.id.emailText) EditText emailText;
    @BindView(R.id.passengerButtonInRegister) RadioButton passengerButton;

    // Used in verification counter
    Handler counterHandler;
    //Determine the running of the thread
    boolean flag = false;

    //API Interface
    APIInterface apiInterface;

    Bitmap thumbnail; //bitmap thumbnail posted on the page
    String imageFilePath; //imageFile Path to upload to the server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_account);
        //Initialize UI Components
        ButterKnife.bind(this);
        //Initialize Counting handler
        counterHandler = new CountingHandler(this);
        //Initialize API Interface
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

//    Select Profile Image
    @OnClick(R.id.profileImg)
    public void selectImg(View v) {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(items[i].equals("Take Photo")) {
                    CreateAccountActivityPermissionsDispatcher.cameraIntentWithPermissionCheck(CreateAccountActivity.this);
                }
                else if(items[i].equals("Choose from Library")) {
                    CreateAccountActivityPermissionsDispatcher.galleryIntentWithPermissionCheck(CreateAccountActivity.this);
                }
                else if(items[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file"), SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case REQUEST_CAMERA:
                    onCaptureImageResult(data);
                    break;
                case SELECT_FILE:
                    onSelectFromGalleryResult(data);
                    break;
                default:
                    break;
            }
        }
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/" + "taxi");
        if (!tmpDir.exists()){
            tmpDir.mkdir();
        }
        File destination = new File(tmpDir.getAbsolutePath(), System.currentTimeMillis() + ".jpg");
        Log.d("Path", destination.getPath());
        imageFilePath = destination.getPath();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        profileImg.setImageBitmap(thumbnail);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                String path = data.getData().getPath();
                Log.d("Path from gallery", path);
                Log.d("Absolute Path", FileUtils.getPath(this, data.getData()));
                imageFilePath = FileUtils.getPath(this, data.getData());
                thumbnail = MediaStore.Images.Media.getBitmap(CreateAccountActivity.this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profileImg.setImageBitmap(thumbnail);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        CreateAccountActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //Check whether all the info is filled
    @OnClick(R.id.sendSMSButton)
    public void checkEmpty(View v) {
        if(TextUtils.isEmpty(usernameText.getText().toString()) ||
                TextUtils.isEmpty(passwordText.getText().toString()) ||
                TextUtils.isEmpty(phoneText.getText().toString())) {
            Toast.makeText(CreateAccountActivity.this, "all the info have to be filled", Toast.LENGTH_LONG).show();
        } else {
//            checkAccountValidity();
            counterText.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.registerButton)
    public void registerAccount (View v) {
        MultipartBody.Part imageFile = null;
        if(imageFilePath != null) {
            File img = new File(imageFilePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), img);
            imageFile = MultipartBody.Part.createFormData("profileImg", img.getName(), requestFile);
        }
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), usernameText.getText().toString());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), passwordText.getText().toString());
        RequestBody phonenumber = RequestBody.create(MediaType.parse("text/plain"), phoneText.getText().toString());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), emailText.getText().toString());

        if(passengerButton.isChecked()) {
            makeRequest("passenger", imageFile, username, password, phonenumber, email);
        } else {
            makeRequest("driver", imageFile, username, password, phonenumber, email);
        }
    }

    private void makeRequest(String identity, MultipartBody.Part file, RequestBody username, RequestBody password, RequestBody phonenumber, RequestBody email) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Processing");
        dialog.show();
        if(identity.equals("passenger")) {
            apiInterface.passengerCreateAccount(file, username, password, phonenumber, email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<AccountResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d(TAG, "onSubscribe: ");
                        }

                        @Override
                        public void onSuccess(AccountResponse response) {
                            dialog.hide();
                            if(response.success == 1) {
                                Toast.makeText(CreateAccountActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                Session.logIn(CreateAccountActivity.this,"passenger", phoneText.getText().toString(), response.access_token);
                                //store the access code, account username, password, phone number
                                //go to the main map activity
                                Intent intent = new Intent(CreateAccountActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(CreateAccountActivity.this, "fail to create account. Your phone number or email may be registered", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.hide();
                            Log.d(TAG, e.getMessage());
                        }

                    });
        } else {
            apiInterface.driverCreateAccount(file, username, password, phonenumber, email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<AccountResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d(TAG, "onSubscribe: ");
                        }

                        @Override
                        public void onSuccess(AccountResponse response) {
                            if(response.success == 1) {
                                dialog.hide();
                                Toast.makeText(CreateAccountActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                Session.logIn(CreateAccountActivity.this, "driver", phoneText.getText().toString(), response.access_token);
                                //store the access code, account username, password, phone number
                                //go to the main map activity
                                Intent intent = new Intent(CreateAccountActivity.this, DriverMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(CreateAccountActivity.this, "fail to create account. Your phone number or email may be registered", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, e.getMessage());
                            dialog.hide();
                        }
                    });
        }
    }

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
