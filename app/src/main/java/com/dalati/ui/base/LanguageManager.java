package com.dalati.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManager {
    private Context ct;
    SharedPreferences sharedPreferences;
    Activity activity;

    public LanguageManager(Context ct) {
        this.ct = ct;
        sharedPreferences = ct.getSharedPreferences("LANG", Context.MODE_PRIVATE);
    }

//Test
    public void updateResources(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Resources res = ct.getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, res.getDisplayMetrics());
        setLocale(code);
        System.out.println(" Changed lang to: " + code);


    }

    public String getLang() {
        return sharedPreferences.getString("lang", "ar");


    }

    public void setLocale(String code) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lang", code);
        editor.commit();
        System.out.println("Nancy new lang saved");

    }


}

