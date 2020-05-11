package com.example.chrysallis.components;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.example.chrysallis.R;

import java.util.Locale;


public class languageManager {

    public static void loadLocale(Context context) {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        Locale myLocale = new Locale(language);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public static String getLocale(Context context){
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");

        return language;
    }

    public static String getLocaleLong(Context context){
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        String languageLong;


        switch (language){
            case "es":
                languageLong = context.getString(R.string.Spanish);
                break;
            case "eu":
                languageLong = context.getString(R.string.Euskera);
                break;
            case "gl":
                languageLong = context.getString(R.string.Galician);
                break;
            case "ca":
                languageLong = context.getString(R.string.Catalan);
                break;
            case "en":
            default:
                languageLong = context.getString(R.string.English);
                break;
        }


        return languageLong;
    }

}
