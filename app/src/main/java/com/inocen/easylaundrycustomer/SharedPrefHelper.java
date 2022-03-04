package com.inocen.easylaundrycustomer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class SharedPrefHelper {
    public SharedPreferences sharedPref;
    public SharedPreferences.Editor editor;
    public Context context;
    public final String counter="counter";
    public final String set="set";

    public SharedPrefHelper(Context context) {
        this.context = context;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void putSharedPref(String... params){
        editor=sharedPref.edit();
        for(int i=0;i<params.length/2;i=i+2){
            editor.putString(params[i], params[i+1]);
        }
        editor.apply();
    }

    public void putSharedPref(String key, boolean value){
        editor=sharedPref.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public void putSharedPref(String key, Set<String> set){
        editor=sharedPref.edit();
        editor.putStringSet(key,set);
        editor.apply();
    }

    public void putSharedPref(String key, int value){
        editor=sharedPref.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public boolean getSharedPref(String key, boolean defaultValue){
        return sharedPref.getBoolean(key,defaultValue);
    }
    public int getSharedPref(String key, int defaultValue){
        return sharedPref.getInt(key,defaultValue);
    }
    public Set<String> getSharedPref(String key){
        return sharedPref.getStringSet(key,null);
    }

    public void removeSharedPref(String key){
        editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }
}
