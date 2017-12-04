package com.mio.app.mioapp.util;

import android.view.View;
import android.view.ViewGroup;

public class Visibility {
	
	
	public static void gone(View v) {
		v.setVisibility(View.GONE);
	}
	
	public static void visible(View v) {
		v.setVisibility(View.VISIBLE);
	}
	
	public static void invisible(View v) {
		v.setVisibility(View.INVISIBLE);
	}
	
	public static void toogle(View v) {
		v.setVisibility(v.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	}
	  public static void enableDisableView(View view, boolean enabled) {
	        view.setEnabled(enabled);
	        if ( view instanceof ViewGroup) {
	            ViewGroup group = (ViewGroup)view;

	            for ( int idx = 0, len = group.getChildCount()  ; idx < len ; ++idx ) {
	                enableDisableView(group.getChildAt(idx), enabled);
	            }
	        }
	    }

}
