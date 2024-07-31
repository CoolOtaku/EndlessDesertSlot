package com.example.endlessdesertslot;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private LinearLayout MenuContainer;
    private ScrollView settingsContainer;

    private ImageView musicOnOfImage;
    private TextView textCoins;
    private SeekBar seekVolume;

    public static int uiOpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideNavBar();

        MenuContainer = (LinearLayout) findViewById(R.id.MenuContainer);
        settingsContainer = (ScrollView) findViewById(R.id.settingsContainer);

        musicOnOfImage = (ImageView) findViewById(R.id.musicOnOfImage);
        textCoins = (TextView) findViewById(R.id.textCoins);
        seekVolume = (SeekBar) findViewById(R.id.seekVolume);

        seekVolume.setMax(100);
        seekVolume.setProgress((int) (Music.volume * 150));

        if (App.sp.getBoolean("MUSIC_ON_OF", true)) {
            Music.play(this, R.raw.main);
            musicOnOfImage.setImageResource(R.drawable.ic_music_note);
        }

        ObjectAnimator scaleDown1 = ObjectAnimator.ofPropertyValuesHolder(MenuContainer,
                PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 1.1f));
        scaleDown1.setDuration(350);
        scaleDown1.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown1.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown1.start();

        seekVolume.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavBar();
    }

    private void hideNavBar(){
        View decor = getWindow().getDecorView();
        uiOpt = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decor.setSystemUiVisibility(uiOpt);
    }

    public void PlayGame(View view) {
        startActivity(new Intent(MainActivity.this, SlotActivity.class));
    }

    public void Settings(View view) {
        if (settingsContainer.getVisibility() != View.VISIBLE) settingsContainer.setVisibility(View.VISIBLE);
        else settingsContainer.setVisibility(View.GONE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        Music.volume = (float) (1 - (Math.log(100 - progress) / Math.log(100)));
        if (Music.mp != null) {
            Music.mp.setVolume(Music.volume, Music.volume);
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        App.SaveVolume();
    }

    public void musicOnOf(View view) {
        if (Music.isPlaying()) {
            Music.stop();
            musicOnOfImage.setImageResource(R.drawable.ic_music_off);
        } else {
            Music.play(this, R.raw.main);
            musicOnOfImage.setImageResource(R.drawable.ic_music_note);
        }
        App.SaveMusicSetting();
    }

    public void ExitGame(View view) {
        finish();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        textCoins.setText(String.valueOf(App.COINS));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (App.sp.getBoolean("MUSIC_ON_OF", true)) Music.play(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.pause();
    }
}