package com.example.endlessdesertslot;

import static com.example.endlessdesertslot.MainActivity.uiOpt;
import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class SlotActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView freeSpinImg;
    private ImageButton infoButton, minusButton, plusButton, maxBetButton, spinButton, stopAutoGameButton, autoGameButton, ExitBTN;
    private TextView betTxt, freeSpinsTxt, winTxt, balanceTxt;
    private ViewFlipper[][] imgFly;
    private List<Integer> listImages;
    private double bet = 0.5, win = 0;
    private int freeSpins = 3;
    private boolean[] slotSpinner = new boolean[]{false, false, false, false, false};
    private boolean autoPlay = false, finish = false;
    private Thread[] flyThread = new Thread[5];
    private Runnable[] flyRunnable = new Runnable[5];
    private Runnable buttonsTurnOn, checkWin;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);
        SystemUINavBarOff();

        if (App.sp.getBoolean("MUSIC_ON_OF", true)) Music.play(this);

        initUIComponents();
        fillImgFly();
        setBetWinBalanceFreeSpins();
        initFlyThreads();
        click();

        flyThread[0].start();
        flyThread[1].start();
        flyThread[2].start();
        flyThread[3].start();
        flyThread[4].start();
    }

    private void initUIComponents() {
        freeSpinImg = findViewById(R.id.free_spin_img);
        infoButton = findViewById(R.id.info_button);
        minusButton = findViewById(R.id.minus_button);
        plusButton = findViewById(R.id.plus_button);
        maxBetButton = findViewById(R.id.max_bet_button);
        spinButton = findViewById(R.id.spin_button);
        stopAutoGameButton = findViewById(R.id.stop_auto_game_button);
        autoGameButton = findViewById(R.id.auto_game_button);
        betTxt = findViewById(R.id.bet_txt);
        freeSpinsTxt = findViewById(R.id.free_spins_txt);
        winTxt = findViewById(R.id.win_txt);
        balanceTxt = findViewById(R.id.balance_txt);
        ExitBTN = findViewById(R.id.ExitBTN);

        imgFly = new ViewFlipper[][]{{findViewById(R.id.img_fly_00), findViewById(R.id.img_fly_01), findViewById(R.id.img_fly_02), findViewById(R.id.img_fly_03), findViewById(R.id.img_fly_04)},
                {findViewById(R.id.img_fly_10), findViewById(R.id.img_fly_11), findViewById(R.id.img_fly_12), findViewById(R.id.img_fly_13), findViewById(R.id.img_fly_14)},
                {findViewById(R.id.img_fly_20), findViewById(R.id.img_fly_21), findViewById(R.id.img_fly_22), findViewById(R.id.img_fly_23), findViewById(R.id.img_fly_24)}};

        listImages = new ArrayList<>(Arrays.asList(R.drawable.a, R.drawable.frog, R.drawable.j, R.drawable.k,
                R.drawable.lizzard, R.drawable.nine, R.drawable.parrot, R.drawable.q,
                R.drawable.scatter, R.drawable.ten, R.drawable.toucan, R.drawable.wild));

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_info_slot);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window windowAlDl = dialog.getWindow();

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.systemUiVisibility = uiOpt;
        windowAlDl.setAttributes(layoutParams);

        Button yes = (Button) dialog.findViewById(R.id.alertbox_yes);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void fillImgFly() {
        int currentIndex = 0;
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 3; i++) {
                imgFly[i][j].setInAnimation(getApplicationContext(), R.anim.slide_up);
                imgFly[i][j].setOutAnimation(getApplicationContext(), R.anim.slide_down);

                for (int f = 0; f < listImages.size(); f++) {
                    ImageView img = new ImageView(SlotActivity.this);
                    img.setImageResource(listImages.get(currentIndex));
                    imgFly[i][j].addView(img);
                    currentIndex = (currentIndex + 1) % 11;
                }
                currentIndex++;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.minus_button:
                minusFunc();
                break;
            case R.id.plus_button:
                plusFunc();
                break;
            case R.id.max_bet_button:
                maxBetFunc();
                break;
            case R.id.spin_button:
                spinFunc();
                break;
            case R.id.auto_game_button:
                autoGameFunc();
                break;
            case R.id.stop_auto_game_button:
                stopAutoGameFunc();
                break;
            case R.id.info_button:
                infoFunc();
                break;
            case R.id.ExitBTN:
                onBackPressed();
                break;
        }
    }

    private void click() {
        infoButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);
        maxBetButton.setOnClickListener(this);
        spinButton.setOnClickListener(this);
        autoGameButton.setOnClickListener(this);
        stopAutoGameButton.setOnClickListener(this);
        ExitBTN.setOnClickListener(this);
    }

    private void unclick() {
        minusButton.setOnClickListener(null);
        plusButton.setOnClickListener(null);
        spinButton.setOnClickListener(null);
        maxBetButton.setOnClickListener(null);
        ExitBTN.setOnClickListener(null);
    }

    private void minusFunc() {
        bet -= 0.50;
        if (bet <= 0)
            bet += 0.50;
        setBetWinBalanceFreeSpins();
    }

    private void plusFunc() {
        bet += 0.50;
        if (bet > App.COINS)
            bet -= 0.50;
        setBetWinBalanceFreeSpins();
    }

    private void maxBetFunc() {
        bet = App.COINS;
        setBetWinBalanceFreeSpins();
    }

    private void spinFunc() {
        Music.sound(SlotActivity.this, R.raw.spin);
        unclick();
        freeSpinImg.setVisibility(View.GONE);
        win = 0;

        if (freeSpins > 0) {
            freeSpins--;
        }

        setBetWinBalanceFreeSpins();

        for (int i = 0; i < 5; i++) {
            slotSpinner[i] = true;
        }
    }

    private void autoGameFunc() {
        autoGameButton.setVisibility(View.GONE);
        autoPlay = true;
        if (!isSinnersMoving()) {
            spinFunc();
        }
    }

    private void stopAutoGameFunc() {
        autoGameButton.setVisibility(View.VISIBLE);
        autoPlay = false;
    }

    private void infoFunc() {
        dialog.show();
    }

    private void setBetWinBalanceFreeSpins() {
        betTxt.setText(String.valueOf(bet));
        winTxt.setText(String.valueOf(win));
        balanceTxt.setText(String.valueOf(App.COINS));
        freeSpinsTxt.setText(String.valueOf(freeSpins));
    }

    private boolean isSinnersMoving() {
        for (int index = 0; index < 5; index++) {
            if (slotSpinner[index] == true) {
                return true;
            }
        }
        return false;
    }

    private void initFlyThreads() {
        for (int k = 0; k < 5; k++) {
            int finalK = k;
            flyRunnable[finalK] = new Runnable() {
                @Override
                public void run() {
                    if (finalK == 0 || finalK == 4) {
                        for (int i = 0; i < 3; i++) {
                            imgFly[i][finalK].showNext();
                        }
                    } else {
                        for (int i = 0; i < 3; i++) {
                            imgFly[i][finalK].showPrevious();
                        }
                    }
                }
            };
        }

        for (int m = 0; m < 5; m++) {
            int finalM = m;
            flyThread[finalM] = new Thread() {
                Random random = new Random();
                int[] speed = new int[3];

                @Override
                public void run() {
                    while (!finish) {
                        while (slotSpinner[finalM]) {
                            bet = Double.parseDouble(betTxt.getText().toString());

                            if (!(App.COINS >= bet)) {
                                slotSpinner[finalM] = false;
                                Helpfulness.runOnUiThread(buttonsTurnOn);
                                break;
                            }

                            for (int i = 0; i < 3; i++) {
                                speed[i] = random.nextInt(7) + 7;
                            }

                            try {
                                int delay = 250;
                                for (int n = 0; n < 3; ++n) {
                                    for (int j = 0; j < speed[n]; ++j) {
                                        Helpfulness.runOnUiThread(flyRunnable[finalM]);
                                        sleep(delay);
                                    }
                                    delay += 70;
                                }
                            } catch (Exception ex) {
                            }

                            slotSpinner[finalM] = false;
                            if (!isSinnersMoving()) {
                                Helpfulness.runOnUiThread(buttonsTurnOn);
                                Helpfulness.runOnUiThread(checkWin);
                            }
                        }
                        try {sleep(35);} catch (InterruptedException e) {e.printStackTrace();}
                    }
                }
            };
        }

        buttonsTurnOn = new Runnable() {
            @Override
            public void run() {
                if (!isSinnersMoving()) {
                    click();
                }
            }
        };

        checkWin = new Runnable() {
            @Override
            public void run() {
                if (!isSinnersMoving()) {
                    int iFly5 = (imgFly[1][0].getDisplayedChild() + 3) % 11;
                    int iFly4 = (imgFly[1][1].getDisplayedChild() + 9) % 11;
                    int iFly3 = (imgFly[1][2].getDisplayedChild() + 4) % 11;
                    int iFly2 = (imgFly[1][3].getDisplayedChild() + 10) % 11;
                    int iFly1 = (imgFly[1][4].getDisplayedChild() + 5) % 11;

                    Log.d("Win Combination", "" + iFly5 + " " + iFly4 + " " + iFly3 + " " + iFly2 + " " + iFly1);

                    int coefficient = -1;

                    List<Integer> centralLineNumbers = new ArrayList<>();
                    Collections.addAll(centralLineNumbers, iFly1, iFly2, iFly3, iFly4, iFly5);
                    int meetAgains = findMeetAgains((ArrayList<Integer>) centralLineNumbers);

                    switch (meetAgains) {
                        case 1:
                            coefficient = 2;
                            break;
                        case 2:
                            coefficient = 3;
                            break;
                        case 3:
                            coefficient = 4;
                            break;
                        case 4:
                            coefficient = 7;
                            break;
                        default:
                            coefficient = -1;
                            break;
                    }

                    freeSpins = Integer.parseInt(freeSpinsTxt.getText().toString());

                    if (coefficient > 0) {
                        win = bet * coefficient;
                        App.COINS += win;
                        Music.sound(SlotActivity.this, R.raw.kassa);
                    } else if (freeSpins > 0) {
                        win = 0;
                    } else {
                        win = 0;
                        App.COINS -= bet;
                    }

                    if (App.COINS <= 0) {
                        try {sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                        App.COINS = 100.00;
                        Toast.makeText(SlotActivity.this, getString(R.string.you_have_points), Toast.LENGTH_LONG).show();
                    }

                    if (bet > App.COINS) {
                        bet = App.COINS;
                    }

                    if (coefficient > 3) {
                        freeSpinImg.setVisibility(View.VISIBLE);
                        freeSpins += 10;
                    }

                    setBetWinBalanceFreeSpins();
                    App.SaveCOINS();

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (autoPlay) {
                        spinFunc();
                    }
                }
            }
        };
    }

    private int findMeetAgains(ArrayList<Integer> numbers) {
        HashSet setSimilars = new HashSet<Integer>();
        int sumMeetAgains = 0;
        for (Integer number : numbers) {
            if (setSimilars.add(number) == false) {
                sumMeetAgains++;
            }
        }
        return sumMeetAgains;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        SystemUINavBarOff();
    }

    private void SystemUINavBarOff() {
        View dv = getWindow().getDecorView();
        dv.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (App.sp.getBoolean("MUSIC_ON_OF", true)) Music.play(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.SaveCOINS();
        Music.pause();
    }

}