package com.mio.app.mioapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mio.app.mioapp.R;
import com.mio.app.mioapp.model.Noticia;
import com.mio.app.mioapp.util.DateEnrutate;
import com.mio.app.mioapp.util.GlobalData;

import java.util.ArrayList;

public class AdapterNoticiasTwitter extends ArrayAdapter<Noticia> {
	

	private LayoutInflater vi;
    Activity activity;
    Typeface robot_regular;
    String screenName = "";

	
	public AdapterNoticiasTwitter(Activity activity, final ArrayList<Noticia> values) {
		super(activity,  R.layout.adapternoticiastwitter, values);
		vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		robot_regular = Typeface.createFromAsset(activity.getAssets(), "RobotoCondensed_Light.ttf");
		if(GlobalData.get("screenname", activity) != null)
			screenName = "@"+GlobalData.get("screenname", activity);		
		
		this.activity = activity;
	}
        public  class ViewHolder {
		private TextView mensaje;
		private TextView fecha;
		private TextView nameAuthor;
	
		private ImageView imagenUser;
		

		public ViewHolder(View v) {
			this.mensaje       = (TextView) v.findViewById(R.id.text_timeline);
			this.nameAuthor    = (TextView) v.findViewById(R.id.name_author_timeline);
			this.fecha         = (TextView) v.findViewById(R.id.date_timeline);
			this.imagenUser = (ImageView) v.findViewById(R.id.image_timeline);
			this.mensaje.setTypeface(robot_regular);
			this.fecha.setTypeface(robot_regular);
			this.nameAuthor.setTypeface(robot_regular, Typeface.BOLD);
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = vi.inflate( R.layout.adapternoticiastwitter, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
       
		final Noticia item = getItem(position);
		if (item != null) 
		{
			holder.mensaje.setText(item.noticia);
			
			if((screenName!="")? item.noticia.contains("#ATENCIÓN") || item.noticia.contains(screenName): item.noticia.contains("#ATENCIÓN")  )
			{
				String horaPublicada = DateEnrutate.getUpdate(item.fecha);
				holder.fecha.setText(""+ horaPublicada);
				holder.nameAuthor.setText("Metrocali");
				holder.imagenUser.setImageResource(R.drawable.metrocali_image);
			}
			else
			{
			   if(item.noticia.contains("#ENRÚTATEMIO"))
			   {
				   String horaPublicada = DateEnrutate.getUpdate(item.fecha);
				   holder.fecha.setText(""+ horaPublicada);
				   if(GlobalData.get("imagetwitter", activity) != null)
				   {
					   holder.nameAuthor.setText(""+GlobalData.get("usuarioTwitter", activity));
				       byte [] b = Base64.decode(GlobalData.get("imagetwitter",activity), Base64.DEFAULT);
					   holder.imagenUser.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
					  
				   }
				   else
				   {
					   holder.nameAuthor.setText("Usuario twitter");
					   holder.imagenUser.setImageResource(R.drawable.banderafin);
				   }
				  
			   }
			   else
			   {
				   holder.nameAuthor.setText("Enrútate MIO");
				   holder.fecha.setText("Publicada el "+item.fecha);
				   holder.imagenUser.setImageResource(R.drawable.ic_launcher);
			   }
				
			   
			}
		}
		return convertView;
	}
	
	
}