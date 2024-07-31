package com.example.endlessdesertslot;

import android.content.Context;
import android.media.MediaPlayer;

public class Music {

    public static MediaPlayer mp = null;
    private static MediaPlayer sp = null;

    public static float volume = 1F;

    public static void play(Context context, int resource) {
        stop();
        mp = MediaPlayer.create(context, resource);
        mp.setLooping(true);
        mp.setVolume(volume, volume);
        mp.start();
    }

    public static void sound(Context context, int resource) {
        sp = MediaPlayer.create(context, resource);
        sp.setVolume(volume, volume);
        sp.start();
    }

    public static void stop() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public static void play(Context context) {
        if (mp != null) {
            mp.start();
        }else{
            play(context, R.raw.main);
        }
    }

    public static void pause() {
        if(mp != null) {
            mp.pause();
        }
    }

    public static boolean isPlaying(){
        if(mp != null){
            return mp.isPlaying();
        }else{
            return false;
        }
    }

}
