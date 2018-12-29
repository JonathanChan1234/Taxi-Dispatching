package com.jonathan.taxidispatching.SharePreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    public static final String LOGGED = "Logged";
    public static final String USERNAME = "username";
    public static final String ACCESS_CODE = "access_code";
    public static final String IDENTITY = "Identity";
    public static final String PHONE_NUMBER = "phone number";

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
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
}
