package com.jonathan.taxidispatching.SharePreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.jonathan.taxidispatching.Model.Transcation;

public class Session {

    public static final String LOGGED = "Logged";
    public static final String USERNAME = "username";
    public static final String ACCESS_CODE = "access_code";
    public static final String IDENTITY = "Identity";
    public static final String PHONE_NUMBER = "phone number";
    public static final String TRANSACTION = "TRANSACTION";
    public static final String TRANSACTION_ID = "TRANSACTION_ID";


    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("Preference", Context.MODE_PRIVATE);
    }

    public static void logIn(Context context, String phonenumber, String identity, String accessCode) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED, true);
        editor.putString(PHONE_NUMBER, phonenumber);
        editor.putString(ACCESS_CODE, accessCode);
        editor.putString(IDENTITY, identity);
        editor.apply();
    }

    public static void logout(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED, false);
        editor.putString(ACCESS_CODE, "");
        editor.apply();
    }

    public static void saveCurrentTransaction(Context context, int id, Transcation.Data transcation) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(TRANSACTION_ID, id);
        Gson gson = new Gson();
        String serializedData = gson.toJson(transcation);
        editor.putString(TRANSACTION, serializedData);
        editor.apply();
    }

    public static int getCurrentTransactionID(Context context) {
        return getPreferences(context).getInt(TRANSACTION_ID, 0);
    }

    public static Transcation.Data getCurrentTranscation(Context context) {
        String serializedData = getPreferences(context).getString(TRANSACTION, "");
        if(!serializedData.isEmpty()) {
            Gson gson = new Gson();
            return gson.fromJson(serializedData, Transcation.Data.class);
        }
        return null;
    }
}
