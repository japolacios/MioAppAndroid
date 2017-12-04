package com.mio.app.mioapp.views;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_layout);
        mContext = this;


        loadTweets();

       // setListAdapter(adapter);
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
