package com.mio.app.mioapp.views;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mio.app.mioapp.R;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;


public class TwitterActivity extends ListActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String CONSUMER_KEY = "iWtpJ2GWSgII8dKH4MPqcDdiw";
    private static final String CONSUMER_SECRET = "fvmPoBIfGNfsT1x40hjZDNZheEZf7xDuIkz9j9FhvIbb5qh0fM";
    private Context mContext;
    private TweetTimelineListAdapter adapter;


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
        setContentView(R.layout.twitter_layout);
        mContext = this;


        loadTweets();

        //Menu Stuff
        menu_out = AnimationUtils.loadAnimation(this, R.anim.menu_out);
        menu_in = AnimationUtils.loadAnimation(this, R.anim.menu_in);
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        menu = (ConstraintLayout) findViewById(R.id.menu);
        blackBg = (ImageView) findViewById(R.id.blackImg);
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

    public void loadTweets() {
        new Thread(new Runnable() {
            public void run() {
                TwitterConfig config = new TwitterConfig.Builder(mContext)
                        .logger(new DefaultLogger(Log.DEBUG))
                        .twitterAuthConfig(new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET))
                        .debug(true)
                        .build();
                Twitter.initialize(config);


                final UserTimeline userTimeline = new UserTimeline.Builder()
                        .screenName("metrocali")
                        .includeReplies(false)
                        .includeRetweets(false)
                        .maxItemsPerRequest(20)
                        .build();
                adapter = new TweetTimelineListAdapter.Builder(mContext)
                        .setTimeline(userTimeline)
                        .build();
                boolean a=  adapter.isEmpty();

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setListAdapter(adapter);
                        }
                    });
                }catch (Exception e){

                }
            }
        }).start();
    }


}
