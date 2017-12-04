package com.mio.app.mioapp.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.Util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mio.app.mioapp.BD.AdminSQLiteOpenHelper;
import com.mio.app.mioapp.R;
import com.mio.app.mioapp.adapter.AdapterNoticiasTwitter;
import com.mio.app.mioapp.fragmentos.FragmentChangeActivity;
import com.mio.app.mioapp.model.Noticia;
import com.mio.app.mioapp.util.AnimationEnrutate;
import com.mio.app.mioapp.util.ByteArrayUtil;
import com.mio.app.mioapp.util.ConnectionDetector;
import com.mio.app.mioapp.util.GlobalData;
import com.mio.app.mioapp.util.KeyBoard;
import com.mio.app.mioapp.util.Mensajes;
import com.mio.app.mioapp.util.TwitterChannel;
import com.mio.app.mioapp.util.Visibility;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import twitter4j.TwitterException;
import twitter4j.User;

@SuppressLint({ "SimpleDateFormat", "InlinedApi", "NewApi" })
public class NoticiasTwitter implements OnRefreshListener {

	public RelativeLayout containerMessage;
	public ImageView imageUser, compose_message, image_news, imageUserTwitter, arrow_back;
	public TextView nameUser, title_news, screenNameTwitter;
	public EditText field_message;
	public Button sendMessage, cancel;
	public ListView listanoticias;
	public View view;
	public ArrayList<Noticia> noticias = new ArrayList<Noticia>();
	public AdapterNoticiasTwitter adapterNoticiasTwitter;
	public TwitterChannel twitterChannel;
	public String verifier;
	public static final String ENCABEZADO = "@METROCALI #ENRÚTATEMIO";
	public SwipeRefreshLayout swipeLayout;
	
	public NoticiasTwitter() {
	}

	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.noticiatwitter);

		containerMessage = (RelativeLayout) findViewById(R.id.containerMessage);
		nameUser = (TextView) findViewById(R.id.nameUser);
		imageUser = (ImageView) findViewById(R.id.imageUser);
		field_message = (EditText) findViewById(R.id.field_message);
		sendMessage = (Button) findViewById(R.id.send_message);
		compose_message = (ImageView) findViewById(R.id.compose_message);
		listanoticias = (ListView) findViewById(R.id.listanoticias);
		cancel = (Button) findViewById(R.id.cancel);
		view = (View) findViewById(R.id.cortina);
		screenNameTwitter = (TextView) findViewById(R.id.screenNameTwitter);
		title_news = (TextView) findViewById(R.id.title_news);
		image_news = (ImageView) findViewById(R.id.image_news);
		imageUserTwitter = (ImageView) findViewById(R.id.imageUserTwitter);
		arrow_back       = (ImageView) findViewById(R.id.arrowBackNews);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
	    swipeLayout.setOnRefreshListener(this);
	    swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
		twitterChannel = TwitterChannel.getInstance(NoticiasTwitter.this);

		verificarDatos();// verifica los datos previos guardados del usuario
							// logueado en twitter

		loginTwitter();// retorno despues del logueo en twitter

		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
			 new cargarNoticias().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		 else
			 new cargarNoticias().execute();
		
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Visibility.gone(containerMessage);
				KeyBoard.hide(field_message);
				Visibility.gone(view);
			}
		});
		imageUserTwitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				twitterChannel.loginTwitter(NoticiasTwitter.this,
						screenNameTwitter, imageUserTwitter);
			}
		});
		screenNameTwitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				twitterChannel.loginTwitter(NoticiasTwitter.this,
						screenNameTwitter, imageUserTwitter);
			}
		});
		arrow_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				launchIntent();
			}
		});
		image_news.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				launchIntent();
			}
		});
		title_news.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				launchIntent();
			}
		});
		sendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// enviar mensaje
				String text = field_message.getText().toString().trim();

				if (text.length() > 0) {
					if (ConnectionDetector
							.isConnectingToInternet(NoticiasTwitter.this)) {
						KeyBoard.hide(field_message);
						new SendingMessage().execute(text);
					} else
						Mensajes.mensajes(NoticiasTwitter.this,
								getResources().getString(R.string.acceso_internet), 1);

				} else
					Mensajes.mensajes(NoticiasTwitter.this,
							"El mensaje no puede ir vacio", 1);

			}
		});

		compose_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (GlobalData.get(TwitterChannel.PREF_KEY_OAUTH_TOKEN,
						NoticiasTwitter.this) == null)
					twitterChannel.loginTwitter(NoticiasTwitter.this,
							screenNameTwitter, imageUserTwitter);
				else {
					if (containerMessage.getVisibility() == View.VISIBLE) {
						containerMessage.setVisibility(View.GONE);
						Visibility.gone(view);
					} else {

						nameUser.setText((GlobalData.get("usuarioTwitter",
								NoticiasTwitter.this) != null) ? GlobalData
								.get("usuarioTwitter", NoticiasTwitter.this)
								: "No disponible");
						if (GlobalData
								.get("imagetwitter", NoticiasTwitter.this) != null) {
							byte[] b = Base64.decode(GlobalData.get(
									"imagetwitter", NoticiasTwitter.this),
									Base64.DEFAULT);
							imageUser.setImageBitmap(BitmapFactory
									.decodeByteArray(b, 0, b.length));
						}
						AnimationEnrutate.fadeIn(containerMessage, 400);
						AnimationEnrutate.fadeIn(view, 300);
					}
				}
			}
		});
		super.onCreate(savedInstanceState);
	}

	
	public void launchIntent() {
		finish();
		Intent intento = new Intent(NoticiasTwitter.this,
				FragmentChangeActivity.class);
		intento.putExtra("fromnews", 1);
		startActivity(intento);
		overridePendingTransition(R.anim.transition_frag_in,
				R.anim.transition_frag_out);
	}
	class cargarNoticias extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		TextView textprogress;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(NoticiasTwitter.this);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
			pDialog.setContentView(R.layout.custom_progress);
			textprogress = (TextView) pDialog.findViewById(R.id.textprogress);
			textprogress.setText("Cargando noticias...");
		}

		protected String doInBackground(String... args) {
			noticias.clear();
			noticias = cargarNoticiasBD();

			return null;
		}

		protected void onPostExecute(String file_url) {

			pDialog.dismiss();

			if (noticias != null && noticias.size() > 0) {
				adapterNoticiasTwitter = new AdapterNoticiasTwitter(
						NoticiasTwitter.this, noticias);
				AnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(
						adapterNoticiasTwitter);
				animAdapter.setAbsListView(listanoticias);
				listanoticias.setAdapter(animAdapter);
			} else
				Mensajes.mensajes(NoticiasTwitter.this,
						"No hay noticias nuevas ó debes conectarte a internet",
						1);

		}

	}
	class RefreshNoticias extends AsyncTask<String, String, String> {

	protected String doInBackground(String... args) {
			noticias.clear();
			noticias = cargarNoticiasBD();

			return null;
		}

		protected void onPostExecute(String file_url) {
			
			if (noticias != null && noticias.size() > 0) {
				adapterNoticiasTwitter = new AdapterNoticiasTwitter(
						NoticiasTwitter.this, noticias);
				AnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(
						adapterNoticiasTwitter);
				animAdapter.setAbsListView(listanoticias);
				listanoticias.setAdapter(animAdapter);
			}
		}

	}

	public ArrayList<Noticia> cargarNoticiasBD() {
		noticias.clear();
		AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(
				NoticiasTwitter.this);
		SQLiteDatabase bd = admin.getWritableDatabase();

		Cursor rs = bd
				.rawQuery(
						"select DISTINCT noticia, fecha, estado from noticias where estado = 'N' or estado = 'R' order by fecha DESC LIMIT 20",
						null);
		if (rs != null) {
			while (rs.moveToNext()) 
					noticias.add(new Noticia(rs.getString(0), rs.getString(1),rs.getString(2)));
			
		}

		rs.close();
		bd.close();
		admin.close();
		return noticias;
	}

	private void insertarNoticia(String noticia, String fecha, String estado,
                                 Activity ac) {
		AdminSQLiteOpenHelper databasehelper = new AdminSQLiteOpenHelper(ac);
		SQLiteDatabase db = databasehelper.getWritableDatabase();

		String query = "INSERT INTO noticias (noticia,fecha,estado) values('"
				+ noticia + "','" + fecha + "','" + estado + "')  ";

		db.execSQL(query);

		db.close();
		databasehelper.close();

	}

	public void loginTwitter() {

		try
		{
			if (twitterChannel.twitter != null) {
				Uri uri = this.getIntent().getData();
				if (uri != null
						&& uri.toString().startsWith(
								TwitterChannel.TWITTER_CALLBACK_URL)) {
					// oAuth verifier
					verifier = uri
							.getQueryParameter(TwitterChannel.URL_TWITTER_OAUTH_VERIFIER);
					if (verifier == null) {
	
						GlobalData.delete("twitterlogin", NoticiasTwitter.this);
						twitterChannel.logoutFromTwitter(NoticiasTwitter.this);
	
					} else {
	
						try {
	
							twitterChannel.accessToken = twitterChannel.twitter
									.getOAuthAccessToken(
											twitterChannel.requestToken, verifier);
							long userID = twitterChannel.accessToken.getUserId();
							twitter4j.User user = twitterChannel.twitter
									.showUser(userID);
							String username = user.getName();
							GlobalData.set("usuarioTwitter", username, this);
							GlobalData.set(TwitterChannel.PREF_KEY_OAUTH_TOKEN,
									twitterChannel.accessToken.getToken(), this);
							GlobalData.set(TwitterChannel.PREF_KEY_OAUTH_SECRET,
									twitterChannel.accessToken.getTokenSecret(),
									this);
	
							Toast.makeText(NoticiasTwitter.this,
									"Terminando proceso de logueo twitter",
									Toast.LENGTH_LONG).show();
							new saveImageProfile().execute();
	
						} catch (Exception e) {
							finish();
							e.printStackTrace();
						}
					}
				}
	
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public byte[] saveDataMainUser() {
		User user;
		try {
			user = twitterChannel.twitter.showUser(twitterChannel.twitter
					.getId());
			String url = user.getBiggerProfileImageURL().replace("bigger",
					"200x200");

			byte[] image = ByteArrayUtil.getBytesURL(url);
			Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 85, baos);
			// this will convert image to byte[]
			byte[] byteArrayImage = baos.toByteArray();
			// this will convert byte[] to string
			String encodedImage = Base64.encodeToString(byteArrayImage,
					Base64.DEFAULT);

			GlobalData.set("imagetwitter", encodedImage, NoticiasTwitter.this);
			GlobalData.set("screenname", "" + user.getScreenName(),
					NoticiasTwitter.this);
			return image;

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return null;

	}

	public void verificarDatos() {
		try
		{
			if (GlobalData.get("screenname", NoticiasTwitter.this) != null)
				screenNameTwitter.setText("@"
						+ GlobalData.get("screenname", NoticiasTwitter.this));
			if (GlobalData.get("imagetwitter", NoticiasTwitter.this) != null) {
				byte[] b = Base64.decode(
						GlobalData.get("imagetwitter", NoticiasTwitter.this),
						Base64.DEFAULT);
				imageUserTwitter.setImageBitmap(BitmapFactory.decodeByteArray(b, 0,
						b.length));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	class saveImageProfile extends AsyncTask<String, String, String> {

		byte[] image = null;

		@Override
		protected String doInBackground(String... params) {
			image = saveDataMainUser();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (image != null) {
				imageUserTwitter.setImageBitmap(BitmapFactory.decodeByteArray(
						image, 0, image.length));
				screenNameTwitter.setText("@"
						+ GlobalData.get("screenname", NoticiasTwitter.this));
			}
		}

	}

	class SendingMessage extends AsyncTask<String, String, String> {
		ProgressDialog pd;
		boolean status = false;
		String text = "";

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(NoticiasTwitter.this);
			pd.setCancelable(false);
			pd.setIndeterminate(false);
			pd.setMessage("Enviando mensaje...");
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			text = params[0];
			status = twitterChannel.sendTweet(ENCABEZADO, "" + params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pd.dismiss();

			if (status) {
				Date date = new Date();
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");

				String fechaFormato = format.format(date);
				insertarNoticia(ENCABEZADO + " " + text, fechaFormato, "N",
						NoticiasTwitter.this);
				new cargarNoticias().execute();
				Visibility.gone(containerMessage);
				field_message.setText("");
				Visibility.gone(view);

			} else
				Mensajes.mensajes(NoticiasTwitter.this,
						"Ocurrió un error al enviar tu mensaje", 1);

		}
	}

	@Override
	public void onBackPressed() {
		launchIntent();
		super.onBackPressed();
	}

	@Override
	public void onRefresh() {
		new lanzarServicioTwitter().execute();
	}
	
	class lanzarServicioTwitter extends AsyncTask<String, String, String>
	{
		boolean status = false;
		@Override
		protected String doInBackground(String... params) {
			status = twitterChannel.refreshTwitter();
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			swipeLayout.setRefreshing(false);
			if(status)
				new RefreshNoticias().execute();
			else
				Toast.makeText(NoticiasTwitter.this, "Tú lista de noticias está actualizada", Toast.LENGTH_SHORT).show();;
		}
	}
	
}
