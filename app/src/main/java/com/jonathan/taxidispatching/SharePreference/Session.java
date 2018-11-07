package com.jonathan.taxidispatching.SharePreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    public static final String LOGGED = "Logged";
    public static final String USERNAME = "username";
    public static final String ACCESS_CODE = "access_code";

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void logIn(Context context, String username, String accessCode) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED, true);
        editor.putString(USERNAME, username);
        editor.putString(ACCESS_CODE, accessCode);
        editor.apply();
    }

    public static void logout(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED, false);
        editor.putString(ACCESS_CODE, "");
        editor.apply();
    }
}
