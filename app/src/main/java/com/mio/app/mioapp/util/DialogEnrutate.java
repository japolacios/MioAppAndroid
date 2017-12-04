package com.mio.app.mioapp.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.enrutatemio.R;

public class DialogEnrutate {


	public static Typeface robot_regular;
	
	public static void createDialog(Activity cxt, String title, String message)
	{
		robot_regular = Typeface.createFromAsset(cxt.getAssets(), "RobotoCondensed_Regular.ttf");
		final Dialog dialog = new Dialog(cxt);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.modal_material_design);
		dialog.setCancelable(false);
		//dialog.setTitle(""+title);

		// set the custom dialog components - text, image and button
		TextView title_modal = (TextView) dialog.findViewById(R.id.title_modal);
		TextView text_modal = (TextView) dialog.findViewById(R.id.text_modal);
		Button dialogButton = (Button) dialog.findViewById(R.id.accept_modal);
		Button dialogButtonCancel = (Button) dialog.findViewById(R.id.cancel_modal);
		title_modal.setText(""+title);
		text_modal.setText(""+message);
		title_modal.setTypeface(robot_regular, Typeface.BOLD);
		text_modal.setTypeface(robot_regular);

		
		dialogButton.setTypeface(robot_regular);
		dialogButtonCancel.setTypeface(robot_regular);
		
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialogButtonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	public static void createDialogCloseApp(final Activity activity, String title, String message)
	{
		robot_regular = Typeface.createFromAsset(activity.getAssets(), "RobotoCondensed_Regular.ttf");
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.modal_material_design);
		dialog.setCancelable(false);
	
		// set the custom dialog components - text, image and button
		TextView title_modal = (TextView) dialog.findViewById(R.id.title_modal);
		TextView text_modal = (TextView) dialog.findViewById(R.id.text_modal);
		Button dialogButton = (Button) dialog.findViewById(R.id.accept_modal);
		Button dialogButtonCancel = (Button) dialog.findViewById(R.id.cancel_modal);
		title_modal.setText(""+title);
		text_modal.setText(""+message);
		title_modal.setTypeface(robot_regular, Typeface.BOLD);
		text_modal.setTypeface(robot_regular);
		Visibility.visible(dialogButtonCancel);

		
		dialogButton.setTypeface(robot_regular);
		dialogButtonCancel.setTypeface(robot_regular);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
				System.exit(0);
				dialog.dismiss();
			}
		});
		
		
		// if button is clicked, close the custom dialog
		dialogButtonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
}
