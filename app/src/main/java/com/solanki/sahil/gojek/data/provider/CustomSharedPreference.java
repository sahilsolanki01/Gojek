package com.solanki.sahil.gojek.data.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CustomSharedPreference {
    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLocation(Context context, String loc){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("loc",loc);
        editor.apply();
    }

    public static String getLocation(Context context){
        return getPreferences(context).getString("loc",null);
    }

}
