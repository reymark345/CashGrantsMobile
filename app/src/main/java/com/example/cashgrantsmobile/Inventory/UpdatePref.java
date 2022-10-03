package com.example.cashgrantsmobile.Inventory;


import android.content.Context;
import android.content.SharedPreferences;


public class UpdatePref {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "xyz";
    private static final String IS_FIRST_TIME_LAUNCH = "firstTime";

    public UpdatePref(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void setIsFirstTimeLaunch(boolean firstTimeLaunch) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, firstTimeLaunch);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}