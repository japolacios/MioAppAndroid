package com.mio.app.mioapp.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class AnimationEnrutate {

	public AnimationEnrutate() {
		// TODO Auto-generated constructor stub
	}
	
	public static void fadeIn(View v , int duration)
	{
		v.setVisibility(View.VISIBLE);
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		//animation.setRepeatMode(Animation.REVERSE);
		//animation.setRepeatCount(Animation.INFINITE);
		animation.setDuration(duration);
		v.startAnimation(animation);
	}
	public static void fadeOut(final View v, int duration)
	{
	
		AlphaAnimation animation = new AlphaAnimation(1, 0);
		//animation.setRepeatMode(Animation.REVERSE);
		//animation.setRepeatCount(Animation.INFINITE);
		animation.setDuration(duration);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				v.setVisibility(View.GONE);
			}
		});
		v.startAnimation(animation);
	}
	public static void translateDown(View v)
	{
		Animation animation = new TranslateAnimation(0, 0,-170, 0);
		animation.setDuration(600);
		animation.setStartOffset(1000);
		animation.setFillBefore(true);
		v.startAnimation(animation);
		
	}
	public static void translateUp(final View v)
	{
		Animation animation = new TranslateAnimation(0, 0,0, -170);
		animation.setDuration(400);
		animation.setFillBefore(true);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				v.setVisibility(View.GONE);
			}
		});
		v.startAnimation(animation);
		
	}

}
