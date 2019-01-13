package com.jonathan.taxidispatching.activity.ui.driver_main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jonathan.taxidispatching.R;
import com.jonathan.taxidispatching.activity.DriverMainActivity;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class DriverMainFragment extends Fragment {
    Activity activity;
    DriverMainDataModel dataModel = new DriverMainDataModel();

    ProgressDialog progressDialog;
    String platenumber;
    Dialog registerDialog, secondDialog, passwordDialog;

    //UI Components
    @BindView(R.id.registerPlateButton)
    Button registerTaxiButton;
    @BindView(R.id.signInPlateButton)
    Button signInButton;
    @BindView(R.id.deleteTaxiAccountButton)
    Button deleteTaxiAccountButton;
    @BindView(R.id.switchFragmentButton)
    Button toWaitingFragmentButton;

    public static DriverMainFragment newInstance() {
        return new DriverMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_main_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        progressDialog = new ProgressDialog(activity);
    }

    @OnClick(R.id.switchFragmentButton)
    public void switchFragment() {
        DriverMainActivity.toWaitingFragment();
    }

    /**
     * Display Register dialog
     * Check whether the taxi was registered before
     */
    @OnClick(R.id.registerPlateButton)
    public void showRegisterDialog(View v) {
        registerDialog = new Dialog(activity);
        registerDialog.setContentView(R.layout.plate_registration_dialog_layout);
        final EditText editText = registerDialog.findViewById(R.id.plateNumberText);
        Button button = registerDialog.findViewById(R.id.plateRegistrationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerDialog.hide();
                progressDialog.show();
                platenumber = editText.getText().toString();
                dataModel.checkDuplicate(platenumber, new DriverMainDataModel.onDataReadyCallBack() {
                    @Override
                    public void onCallBack(String message) {
                        progressDialog.hide();
                        if(message.equals("success")) {
                            showPasswordDialog();
                        } else {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        registerDialog.show();
    }

    /**
     * Display Password Dialog
     * If the taxi has not been registered before, enter the password for verification
     */
    private void showPasswordDialog() {
        secondDialog = new Dialog(activity);
        secondDialog.setContentView(R.layout.plate_registration_dialog_layout);
        final EditText editText = secondDialog.findViewById(R.id.plateNumberText);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        TextView title = secondDialog.findViewById(R.id.titleText);
        title.setText("Choose a password");
        Button button = secondDialog.findViewById(R.id.plateRegistrationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                dataModel.registerAccount(platenumber, editText.getText().toString(), 5, new DriverMainDataModel.onDataReadyCallBack() {
                    @Override
                    public void onCallBack(String message) {
                        progressDialog.hide();
                        secondDialog.dismiss();
                        if(message.equals("success")) {
                            AlertDialog dialog = new AlertDialog.Builder(activity)
                                    .setTitle("Taxi account Registration")
                                    .setMessage("Please download the Taxi QR code App for sign In")
                                    .show();
                        } else {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        secondDialog.show();
    }

    /**
     * Store the QR code image into the external storage
     * @param qrCodeImage QR Code image in bitmap
     * @param platenumber Image name
     */
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void storeQRCodeImage(Bitmap qrCodeImage, String platenumber) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        qrCodeImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/" + "taxi");
        if (!tmpDir.exists()){
            tmpDir.mkdir();
        }
        File destination = new File(tmpDir.getAbsolutePath(), platenumber + ".jpg");
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
    }

    /**
     * Scan QR code
     */
    @OnClick(R.id.signInPlateButton)
    @NeedsPermission(Manifest.permission.CAMERA)
    public void initQRCodeScanner(View v) {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan the Label");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    /**
     * Enter password for sign in confirmation
     */
    private void showEnterPasswordDialog(final String accessToken) {
        passwordDialog = new Dialog(activity);
        passwordDialog.setContentView(R.layout.plate_registration_dialog_layout);
        final EditText editText = passwordDialog.findViewById(R.id.plateNumberText);
        TextView title = passwordDialog.findViewById(R.id.titleText);
        title.setText("Enter the taxi account plate number");
        Button button = passwordDialog.findViewById(R.id.confirmationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataModel.signInTaxi(editText.getText().toString(), accessToken, 5, new DriverMainDataModel.onDataReadyCallBack() {
                    @Override
                    public void onCallBack(String message) {
                        if(message.equals("success")) {
                            DriverMainActivity.toWaitingFragment();
                        } else {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        secondDialog.show();
    }

    /**
     * Allow the users select which taxi account they want to remove
     */
    @OnClick(R.id.deleteTaxiAccountButton)
    public void deleteAccount() {
        dataModel.checkOwnTaxi(5, new DriverMainDataModel.onListCallBack() {
            @Override
            public void onCallBack(final String[] data) {
                final AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Please select a taxi you owned");
                if(data == null) {
                    builder.setMessage("Network connection fail");
                } else {
                    builder.setItems(data, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LayoutInflater inflater = LayoutInflater.from(activity);
                            final View v = inflater.inflate(R.layout.text_input_layout, null);
                            AlertDialog passwordDialog = new AlertDialog.Builder(activity)
                                    .setView(v)
                                    .setTitle("Please enter your password of the taxi account")
                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            EditText editText = v.findViewById(R.id.editText);
                                            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                                            dataModel.deleteTaxiAccount(data[i], editText.getText().toString(), new DriverMainDataModel.onDataReadyCallBack() {
                                                @Override
                                                public void onCallBack(String message) {
                                                    if(message.equals("success")) {
                                                        Toast.makeText(activity, "Delete successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(activity, "Fail. Please try again", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .show();
                        }
                    });
                }
                dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("QR Code", "Cancelled");
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("QR Code", "Scanned: " + result.getContents());
                showEnterPasswordDialog(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
