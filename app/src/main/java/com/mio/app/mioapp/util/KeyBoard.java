package com.mio.app.mioapp.util;

import android.app.Service;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyBoard {
	public static void hide(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void show(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
		
	}
}
