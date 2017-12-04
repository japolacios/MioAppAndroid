/**
 * @TODO
 * metodo para responder a un post en twitter
 *   StatusUpdate st = new StatusUpdate("hello");
     st.inReplyToStatusId(twitter.getId());
     twitter.updateStatus(st);
 */
package com.mio.app.mioapp.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mio.app.mioapp.BD.AdminSQLiteOpenHelper;
import com.mio.app.mioapp.R;
import com.mio.app.mioapp.actividades.NoticiasTwitter;

import java.io.File;
import java.util.ArrayList;

import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @class TwitterChannel
 * @author edwin.ospina@iptotal.com( Edwin Ramiro Ospina) -
 *         juan.bernal@iptotal.com( Juan Martín Bernal)
 * @date 19 de marzo 2014
 * 
 * @version 1.0
 * 
 * @Licence Derechos reservados de Autor (c) IP Total Software S.A.
 * 
 * @description se encarga de la lectura, salida de tweets
 */
public class TwitterChannel {

	public AccessToken accessToken;
	public Twitter twitter;
	public RequestToken requestToken;
	public String username = "";
	public Intent page;

	private static final int ID_NOTIFICACION_CREAR = 1;
	public ArrayList<String> noticias = new ArrayList<String>();
	private NotificationManager notManager;
	
	public static final String CHANNEL_TYPE = "T";
	public static Typeface robot_regular;
	
	private static Context context;


	static String TWITTER_CONSUMER_KEY = "80pYIXHroG5iZxBXnKkBsA"; //enrutate
	static String TWITTER_CONSUMER_SECRET = "7z4gV41dpCSBhLDLxLrIBkoMBo8fyMiwQhHqN5rFc8"; //enrutate
	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

	public static final String TWITTER_CALLBACK_URL = "oauth://mio.app.mioapp";

	// Twitter oauth urls
	public static final String URL_TWITTER_AUTH = "auth_url";
	public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	public static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	// instancia �nica del objeto TwitterChannel, patr�n Singleton
	private static TwitterChannel TWITTER_CHANNEL = null;

	public static String getToken(Context cxt)
	{
		return GlobalData.get(TwitterChannel.PREF_KEY_OAUTH_SECRET, cxt);
	}
	/**
	 * permite retornar una unica instancia del objeto TwitterChannel, patr�n
	 * Singleton
	 * 
	 * @return TWITTER_CHANNEL , �nica instancia del objeto TwitterChannel
	 */
	public static TwitterChannel getInstance(Context c) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if (TWITTER_CHANNEL == null)
			TWITTER_CHANNEL = new TwitterChannel(c);

		return TWITTER_CHANNEL;

	}

	// evita que el objeto pueda ser clonado
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * constructor privado, patr�n Singleton
	 * 
	 * @param c
	 * 
	 */
	private TwitterChannel(Context c) {
		this.context = c;
		try
		{
			//Log.d("credentials_twitter", "** "+GlobalData.get(PREF_KEY_OAUTH_TOKEN, context) +  " " +GlobalData.get(PREF_KEY_OAUTH_SECRET, context));
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
			configurationBuilder
					.setDebugEnabled(true)
					.setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
					.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET)
					.setOAuthAccessToken(
							GlobalData.get(PREF_KEY_OAUTH_TOKEN, context))
					.setOAuthAccessTokenSecret(
							GlobalData.get(PREF_KEY_OAUTH_SECRET, context));
	
			TwitterFactory twitterFactory = new TwitterFactory(
					configurationBuilder.build());
			twitter = twitterFactory.getInstance();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	/**
	 * Check user already logged in your application using twitter Login flag is
	 * fetched from Shared Preferences
	 * */
	public boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return Boolean.parseBoolean(GlobalData.get(PREF_KEY_TWITTER_LOGIN,
				context));
	}

	/**
	 * Accion login y logout twitter
	 * 
	 * @param ac
	 * @param
	 */
	public void loginTwitter(final Activity ac, final TextView user, final ImageView image) {
		if (!isTwitterLoggedInAlready()) {

			if(ConnectionDetector.isConnectingToInternet(ac))
				loginTwitterPage(ac);
			else
				Mensajes.mensajes(ac, "Debes tener conexión a internet", 1);

		} else {
			String username = GlobalData.get("usuarioTwitter", context);
			if (username != null) {
				
				
					robot_regular = Typeface.createFromAsset(ac.getAssets(), "RobotoCondensed_Regular.ttf");
					final Dialog dialog = new Dialog(ac);
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
					title_modal.setText("Cerrar sesión");
					text_modal.setText("¿Deseas cerrar "+username + "?");
					title_modal.setTypeface(robot_regular, Typeface.BOLD);
					text_modal.setTypeface(robot_regular);
					Visibility.visible(dialogButtonCancel);
	
					
					dialogButton.setTypeface(robot_regular);
					dialogButtonCancel.setTypeface(robot_regular);
					// if button is clicked, close the custom dialog
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// logoutFromTwitter();
							new logoutTwitterAsync(ac).execute();
							
							user.setText("Iniciar twitter");
							image.setImageResource(R.drawable.canal_twitter);
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
			} else {
				GlobalData.delete(PREF_KEY_OAUTH_TOKEN, context);
				GlobalData.delete(PREF_KEY_OAUTH_SECRET, context);
				GlobalData.delete(PREF_KEY_TWITTER_LOGIN, context);
				GlobalData.delete("usuarioTwitter", context);
				loginTwitterPage(ac);
			}

		}
	}

	private void loginTwitterPage(Activity ac) {
		// TODO Auto-generated method stub

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder
				.setDebugEnabled(true)
				.setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
				.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET)
				.setOAuthAccessToken(
						GlobalData.get(PREF_KEY_OAUTH_TOKEN, context))
				.setOAuthAccessTokenSecret(
						GlobalData.get(PREF_KEY_OAUTH_SECRET, context));

		TwitterFactory twitterFactory = new TwitterFactory(
				configurationBuilder.build());
		twitter = twitterFactory.getInstance();

		if (twitter != null) 
			new LaunchWebview(ac).execute();

		
	}

	public void logoutFromTwitter(Context context) {
		GlobalData.delete(PREF_KEY_OAUTH_TOKEN, context);
		GlobalData.delete(PREF_KEY_OAUTH_SECRET, context);
		GlobalData.delete(PREF_KEY_TWITTER_LOGIN, context);
		GlobalData.delete("usuarioTwitter", context);
		GlobalData.delete("imagetwitter", context);
		GlobalData.delete("screenname",context);
		
	}

	private ArrayList<String> almacenaTweets = new ArrayList<String>();

	public static AccessToken loadAccessToken(String token, String tokenSecret) {
		return new AccessToken(token, tokenSecret);
	}

	/**
	 * publica un twit nombrando al usuario
	 * 
	 * @param username
	 * @param text
	 */
	public boolean sendTweet(String username, String text) {

		boolean status = false;
		String sendMessage = username;
		String[] tokens = text.split(" ");
		String continues = "...Continúa";

		for (String token : tokens) {
			String next = sendMessage + " " + token;

			if (next.length() > 140 - (continues.length())) {

				sendMessage = sendMessage + continues;
				almacenaTweets.add(sendMessage);
				sendMessage = username + " " + token;
			} else
				sendMessage = sendMessage + " " + token;
			
		}

		if (!"".equals(sendMessage)) 
			almacenaTweets.add(sendMessage);
		
		for (int i = almacenaTweets.size() - 1; i >= 0; --i) {
			try {
				twitter.updateStatus(almacenaTweets.get(i).toString());
				status = true;
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;
	}

	/**
	 * obtiene las menciones del usuario
	 * 
	 * NOTA: solo se debe usar luego del usuario iniciar sessi�n
	 * 
	 * @return List<Tweet> o null en caso de error
	 */
	public ResponseList<Status> getMentions(int from, int until, Activity ac) {
		Paging paging = new Paging(from, until);
		ResponseList<Status> menciones = null;

		try {
			menciones = twitter.getMentionsTimeline(paging);
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		

		return menciones;
	}

	/**
	 * obtiene los mensajes directos de la cuenta del usuario
	 * 
	 * NOTA: solo se debe usar luego del usuario iniciar sessi�n
	 * 
	 * @return List<Menssage>
	 */
	public ResponseList<DirectMessage> getAllDirectMessages() {
		ResponseList<DirectMessage> directMessages = null;

		try {
			directMessages = twitter.getDirectMessages();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return directMessages;
	}


	/**
	 * funcionalidad de retwittear status(Tweetts)
	 * 
	 * @param idStatus
	 *            , id del status a retwittear
	 * @return true , si el retweet fue exitoso de lo contrario false
	 */
	public String retweet(long idStatus) {

		String idRetweet = null;
		try {
			Status status = twitter.retweetStatus(idStatus);
			idRetweet = String.valueOf(status.getId());
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return idRetweet;

	}

	/**
	 * funcionalidad de hacer favorito un tweet
	 * 
	 * @param idStatus
	 *            , id del status favorito
	 * @return true , si el favorito fue exitoso de lo contrario false
	 */
	public boolean createFavorite(long idStatus) {

		try {
			twitter.createFavorite(idStatus);
			return true;
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * funcionalidad de borrar un favorito
	 * 
	 * @param idStatus
	 *            , id del status favorito
	 * @return true , si el favorito fue exitoso al borrarlo de lo contrario
	 *         false
	 */
	public boolean destroyFavorite(long idStatus) {

		try {
			twitter.destroyFavorite(idStatus);
			return true;
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * funcionalidad de borrar un retweet
	 * 
	 * @param idStatus
	 *            , id del status retweet
	 * @return true , si el retweet fue exitoso al borrarlo de lo contrario
	 *         false
	 */
	public boolean destroyRetweet(long idStatus) {

		try {
			twitter.destroyStatus(idStatus);

			return true;
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * provee un metodo para enviar un mensaje directo.
	 * 
	 * @param user
	 *            , es el nombre de usuario en twitter (@username_example)
	 * @param message
	 *            , mensaje a enviar
	 * @return true , si el envio fue exitoso de lo contrario false
	 */
	public boolean sendMessageDirect(String user, String message) {

		try {
			twitter.sendDirectMessage(user, message);
			return true;
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * To upload a picture with some piece of text.
	 * 
	 * 
	 * @param file
	 *            The file which we want to share with our tweet
	 * @param message
	 *            Message to display with picture
	 * @param
	 *
	 * 
	 */

	public void uploadPic(File file, String message) {

		try {
			StatusUpdate status = new StatusUpdate(message);

			if (file != null) {
				status.setMedia(file);
				
			}
			twitter.updateStatus(status);

		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	

	public static Context getContext() {
		return context;
	}

	public static void setContext(Activity context) {
		TwitterChannel.context = context;
	}

	class logoutTwitterAsync extends AsyncTask<String, String, String> {

	
		public Context contexto;
		public logoutTwitterAsync(Context cxt) {
			// TODO Auto-generated constructor stub
			this.contexto = cxt;
		}
	

		protected String doInBackground(String... args) {

			logoutFromTwitter(contexto);
			return null;
		}


	}
	class LaunchWebview extends AsyncTask<String, String, String> {

		ProgressDialog pd;
		Uri uri;
		boolean status = false;
        Activity activity;
		public LaunchWebview(Activity activity) {
			this.activity = activity;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pd = new ProgressDialog(activity);
			pd.setMessage("Cargando twitter...");
			pd.setCancelable(false);
			pd.show();
		}

		protected String doInBackground(String... args) {

			try {
				requestToken = twitter
						.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				uri = Uri.parse(requestToken.getAuthenticationURL());
				
				GlobalData.set(PREF_KEY_TWITTER_LOGIN, "true", activity);
				GlobalData.set("twitterlogin", "true", activity);
				status = true;
				
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
				
				return "";
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {

			pd.dismiss();
			if(status)
			{
				if(ConnectionDetector.isConnectingToInternet(activity))
				{
					if (status && uri != null) {
						page = new Intent(Intent.ACTION_VIEW, uri);
						activity.startActivity(page);
						
					} else
						Toast.makeText(getContext(),
								"Url invalida",
								Toast.LENGTH_LONG).show();
				}
				else
					Toast.makeText(getContext(),
							"Revisa tu conexión a internet",
							Toast.LENGTH_LONG).show();
			}
			else
				Toast.makeText(getContext(),
						"Error al abrir la página de twitter",
						Toast.LENGTH_LONG).show();
		}

	}
	private void insertarNoticiaTwitter(String noticia, String fecha, String estado,
                                        Context s) {
		AdminSQLiteOpenHelper databasehelper = new AdminSQLiteOpenHelper(s);
		SQLiteDatabase db = databasehelper.getWritableDatabase();

		String query = "INSERT INTO noticias (noticia,fecha,estado) values('"
				+ noticia + "','" + fecha + "','" + estado + "')  ";

		db.execSQL(query);

		db.close();
		databasehelper.close();

	} 
	
	public  void connectTwitter() {
	
		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("0C664TwpmH3rJSAdSM0SukZXA");
		cb.setOAuthConsumerSecret("mEBmxmb3tBUzgPKBNedb7ijIOlO0O75Dqil1kyuCYnrfk3ySDm");
		cb.setOAuthAccessToken("1650923978-HVPeNQLu2qvCe2J8B8ShSSkhZ9GVdHhyTAXalRJ");
		cb.setOAuthAccessTokenSecret("wyi5QhzB1Gpo5mOIzSQmH4hknDjNQK8m7S9sNIJ1eV8jd");
		
		Twitter unauthenticatedTwitter = new TwitterFactory(cb.build())
				.getInstance();

		noticias = comparar(context);
		try {
			ResponseList<Status> statuses = unauthenticatedTwitter
					.getUserTimeline("METROCALI");// username de la cuenta que
													// se quiere hacer
													// seguimiento de su TIMELINE
			String screenName = "";
			if(GlobalData.get("screenname", context) != null)
				screenName = "@"+GlobalData.get("screenname", context);		
			
			int contador = 0;
			String news = "";
			for (Status status : statuses) {
				
				 if((screenName!="")? status.getText().contains("#ATENCIÓN") || status.getText().contains(screenName): status.getText().contains("#ATENCIÓN")  )
				 {
					 if (!noticias.contains(status.getText())) {

					
							while (noticias.size() > 50) {
							
								 
							     if(noticias.get(noticias.size() - 1).contains("#ATENCIÓN"))
							     {
							    	
							    	 deleteTweets(noticias.get(noticias.size() - 1));
							    	 noticias.remove(noticias.get(noticias.size() - 1));
							     }
							     else   
							     {
							    	
							    	 updateNoticia(noticias.get(noticias.size() - 1)); 
							    	 noticias.remove(noticias.get(noticias.size() - 1));
							     }
							     Log.d("twitternoticias", "actualizando data: " + noticias.size());
							     
							}

							String tmp = DateEnrutate.formatearFecha("yyyy-MM-dd HH:mm:ss", status.getCreatedAt().getTime());
							insertarNoticiaTwitter(status.getText(),tmp,"N",context);
							news = status.getText();
							++contador;
							
					 }
				 }
			}
			if(contador != 0)
			{
			  notificar2(context, news);
			  news = "";
			}

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public  boolean refreshTwitter() {
		boolean statusTweets = false;
		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("0C664TwpmH3rJSAdSM0SukZXA");
		cb.setOAuthConsumerSecret("mEBmxmb3tBUzgPKBNedb7ijIOlO0O75Dqil1kyuCYnrfk3ySDm");
		cb.setOAuthAccessToken("1650923978-HVPeNQLu2qvCe2J8B8ShSSkhZ9GVdHhyTAXalRJ");
		cb.setOAuthAccessTokenSecret("wyi5QhzB1Gpo5mOIzSQmH4hknDjNQK8m7S9sNIJ1eV8jd");
		
		Twitter unauthenticatedTwitter = new TwitterFactory(cb.build())
				.getInstance();

		noticias = comparar(context);
		try {
			ResponseList<Status> statuses = unauthenticatedTwitter
					.getUserTimeline("metrocali");// username de la cuenta que
													// se quiere hacer
													// seguimiento de su
												// Timeline
			String screenName = "";
			if(GlobalData.get("screenname", context) != null)
				screenName = "@"+GlobalData.get("screenname", context);
			
			int contador = 0;
			for (Status status : statuses) {
				
				 if((screenName!="")? status.getText().contains("#ATENCIÓN") || status.getText().contains(screenName): status.getText().contains("#ATENCIÓN")  )
				 {
					 if (!noticias.contains(status.getText())) {

					
							while (noticias.size() > 50) {
							
								 
							     if(noticias.get(noticias.size() - 1).contains("#ATENCIÓN"))
							     {
							    	
							    	 deleteTweets(noticias.get(noticias.size() - 1));
							    	 noticias.remove(noticias.get(noticias.size() - 1));
							     }
							     else   
							     {
							    	
							    	 updateNoticia(noticias.get(noticias.size() - 1)); 
							    	 noticias.remove(noticias.get(noticias.size() - 1));
							     }
							     Log.d("twitternoticias", "actualizando data: " + noticias.size());
							     
							}

							String tmp = DateEnrutate.formatearFecha("yyyy-MM-dd HH:mm:ss", status.getCreatedAt().getTime());
							insertarNoticiaTwitter(status.getText(),tmp,"N",context);
							++contador;
					 }
				 }
			}
			if(contador != 0)
				statusTweets = true;

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			statusTweets = false;
			e.printStackTrace();
			return statusTweets;
		}
		return statusTweets;
		
	}
	public void updateNoticia(String noticia)
	{
		try
		{
			AdminSQLiteOpenHelper data = new AdminSQLiteOpenHelper(context);
			SQLiteDatabase db = data.getWritableDatabase();
			String query = "UPDATE noticias SET estado = 'Y' where noticia = '"+noticia+"'";
			db.execSQL(query);
			db.close();
			data.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void deleteTweets(String tweet)
	{
	
		try
		{
			AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context);
		    SQLiteDatabase bd           = admin.getWritableDatabase();
			
			
			String query = "DELETE from noticias where noticia = '"+tweet+"'";
			
			bd.execSQL(query);
			
			bd.close();
			admin.close();
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	private void notificar(Context s) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean result = sharedPreferences.getBoolean("notificationsounds",true);
		boolean vibrate = sharedPreferences.getBoolean("preferenciavibracion",true);
		
		int id = (int) System.currentTimeMillis();

		String ns = s.NOTIFICATION_SERVICE;
		notManager = (NotificationManager) s.getSystemService(ns);

		// Configuramos la notificaci�n
		int icono = R.drawable.inicio;
		CharSequence textoEstado = "Hay nuevas noticias";
		long hora = System.currentTimeMillis();

		Notification notif = new Notification(icono, textoEstado, hora);

		CharSequence titulo = "Enrútate Mio";
		CharSequence descripcion = "Hay nuevas noticias";

		Intent notIntent = new Intent(s, NoticiasTwitter.class);
	//	notIntent.putExtra("noti", 1);
		
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contIntent = PendingIntent.getActivity(s, id, notIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		//notif.setLatestEventInfo(s, titulo, descripcion, contIntent);

		// AutoCancel: cuando se pulsa la notificai�n ésta desaparece
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
       
		// Añadir sonido, vibraci�n y luces
		if(result)
			notif.defaults |= Notification.DEFAULT_SOUND;
		
		if(vibrate)
			notif.defaults |= Notification.DEFAULT_VIBRATE;
		//notif.defaults |= Notification.DEFAULT_ALL;

		// Enviar notificaci�n
		GlobalData.set("notification", "Y", s);
		notManager.notify(ID_NOTIFICACION_CREAR, notif);
	

	}
	private void notificar2(Context s, String message)
	{
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean result = sharedPreferences.getBoolean("notificationsounds",true);
		boolean vibrate = sharedPreferences.getBoolean("preferenciavibracion",true);
		
		Uri sound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder nb = new NotificationCompat.Builder(
				context);
		nb.setSmallIcon(R.drawable.inicio);

		if (result)
			nb.setSound(sound);

		nb.setLights(Color.BLUE, 500, 500);
		nb.setAutoCancel(true);
		nb.setContentTitle("Enrútate MIO");
		nb.setContentText(message);

		nb.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
		GlobalData.set("notification", "1", context);
		nb.setContentIntent(PendingIntent.getActivity(context, 100,
				new Intent(context, NoticiasTwitter.class), 0));

		NotificationManager nm = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);
		nm.notify(100, nb.build());
	}
	public ArrayList<String> comparar(Context s) {
		try {
			noticias.clear();

			AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(s);
			SQLiteDatabase db = admin.getWritableDatabase();

			// Creamos el cursor
			Cursor c = db.rawQuery("select noticia from noticias", null);
			if (c != null) {
				while (c.moveToNext()) 
					noticias.add(c.getString(0));
			}
			c.close();
			db.close();
			admin.close();
		}

		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		return noticias;

	}

}