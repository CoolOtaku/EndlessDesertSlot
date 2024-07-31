package com.example.endlessdesertslot;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application {

    public static double COINS = 100.00;

    public static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();
        COINS = (double) sp.getFloat("COINS", 100.00F);
        Music.volume = sp.getFloat("VOLUME", 1F);
    }

    public static void SaveCOINS() {
        editor.putFloat("COINS", (float) COINS);
        editor.apply();
    }

    public static void SaveMusicSetting() {
        editor.putBoolean("MUSIC_ON_OF", Music.isPlaying());
        editor.apply();
    }

    public static void SaveVolume() {
        editor.putFloat("VOLUME", Music.volume);
        editor.apply();
    }

}
