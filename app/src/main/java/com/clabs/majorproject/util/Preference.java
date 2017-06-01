package com.clabs.majorproject.util;


import android.content.Context;
import android.content.SharedPreferences;

public class Preference {

    public static String getUserId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.USER_ID_KEY, "#");
    }

    public static String getUserName(Context context) {
        return "Shubham Nandanwar";
    }

    public static String getCity(Context context) {
        return "Indore";
    }

    public static String getStoreId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.STORE_ID_KEY, "#");

    }
    public static String getStoreType(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.STORE_TYPE_ID_KEY, "#");
    }

    public static String getImageUri(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.IMAGE_URI_KEY, "#");
    }


}
