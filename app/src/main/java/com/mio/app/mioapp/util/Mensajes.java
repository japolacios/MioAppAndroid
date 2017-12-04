package com.mio.app.mioapp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mio.app.mioapp.R;

public class Mensajes 
{

	static Typeface robot_regular;
	
	public  static void mensajes(Context a , String message, int opc){

		robot_regular = Typeface.createFromAsset(a.getAssets(), "RobotoCondensed_Regular.ttf");
        LayoutInflater inflater = ((Activity) a).getLayoutInflater();

         View layout = inflater.inflate(R.layout.toast_layout,
         (ViewGroup) ((Activity) a).findViewById(R.id.toastLayout));
        
         TextView text = (TextView) layout.findViewById(R.id.mensajetoast);
         text.setText(message);
         text.setTypeface(robot_regular);
         Toast toast = new Toast(a);
         toast.setGravity(Gravity.CENTER, 0, 0);
         
         if(opc == 0)
        	 toast.setDuration(Toast.LENGTH_LONG);
         else
        	 toast.setDuration(Toast.LENGTH_SHORT);
        
         toast.setView(layout);
         toast.show();
    }
	
	public  static void mensajes(Context a , String message, int icon , int opc){

        LayoutInflater inflater = ((Activity) a).getLayoutInflater();

         View layout = inflater.inflate(R.layout.toast_layout,
         (ViewGroup) ((Activity) a).findViewById(R.id.toastLayout));
        
         TextView text = (TextView) layout.findViewById(R.id.mensajetoast);
         ImageView image = (ImageView) layout.findViewById(R.id.image_toast);
         text.setText(message);
         image.setImageResource(icon);
         
         Toast toast = new Toast(a);
         toast.setGravity(Gravity.CENTER, 0, 0);
         
         if(opc == 0)
        	 toast.setDuration(Toast.LENGTH_LONG);
         else
        	 toast.setDuration(Toast.LENGTH_SHORT);
        
         toast.setView(layout);
         toast.show();
    }
}
