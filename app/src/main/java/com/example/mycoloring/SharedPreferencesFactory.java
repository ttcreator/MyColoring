package com.example.mycoloring;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesFactory {

    private static final String Name = "Cache";
    public static final String SavedColor1 = "saved_color1";
    public static final String SavedColor2 = "saved_color2";
    public static final String SavedColor3 = "saved_color3";

    public static int getInteger(Context context, String key, Integer def) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Name, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, def);
    }

    public static void saveInteger(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void saveStringUrl (Context context, String key, String url) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, url);
        editor.commit();
    }

    public static void saveStringName (Context context, String key, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, name);
        editor.commit();
    }

    public static String getStringUrl (Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

}
