package com.mio.app.mioapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mio.app.mioapp.R;

public class ConfigActivity extends AppCompatActivity {


    //Menu stuff
    private Animation menu_out;
    private Animation menu_in;
    private Animation fade_out;
    private Animation fade_in;
    private ConstraintLayout menu;
    private ImageView blackBg;
    private boolean menuVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //Menu Stuff
        menu_out = AnimationUtils.loadAnimation(this, R.anim.menu_out);
        menu_in = AnimationUtils.loadAnimation(this, R.anim.menu_in);
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        menu = (ConstraintLayout) findViewById(R.id.menu);
        blackBg = (ImageView) findViewById(R.id.blackImg2);
        // setListAdapter(adapter);
        menu.startAnimation(menu_out);
        blackBg.startAnimation(fade_out);
        new CountDownTimer(600, 100) {


            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("TICK", "onTick: ");
            }

            public void onFinish() {
                menu.setVisibility(View.GONE);
                blackBg.setVisibility(View.GONE);
                menuVisible = false;

            }
        }.start();
    }
    public void toogleMenu(View view){
        Log.d("MENU", "toogleMenu: HIT");

        if (menuVisible){
            menu.startAnimation(menu_out);
            blackBg.startAnimation(fade_out);
            new CountDownTimer(600, 100) {


                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d("TICK", "onTick: ");
                }

                public void onFinish() {
                    menu.setVisibility(View.GONE);
                    blackBg.setVisibility(View.GONE);

                    menuVisible = false;
                }
            }.start();
        } else{
            if (!menuVisible){
                menu.startAnimation(menu_in);
                menu.setVisibility(View.VISIBLE);
                blackBg.startAnimation(fade_in);
                blackBg.setVisibility(View.VISIBLE);
                new CountDownTimer(600, 100) {


                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.d("TICK", "onTick: ");
                    }

                    public void onFinish() {
                        menuVisible = true;
                    }
                }.start();
            }
        }

    }

    public void gotoMaps(View view){
        Intent i = new Intent(this, live_view.class);
        startActivity(i);

    }

    public void gotoNews(View view){
        Intent i = new Intent(this, TwitterActivity.class);
        startActivity(i);

    }
}
