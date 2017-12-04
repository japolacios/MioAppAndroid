package com.mio.app.mioapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.enrutatemio.R;
import com.enrutatemio.mapa.ShowMapActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MapGoogle implements OnMapReadyCallback {

	private final static int ZOOM_LEVEL = 12;

	public GoogleMap map;
	public ShowMapActivity showMapActivity;
	private OnMapClickListener onMapClickListener;
	private OnMarkerClickListener onMarkerClickListener;
	private OnInfoWindowClickListener onInfoWindowClickListener;

	public MapGoogle(Fragment activity, ShowMapActivity showMapActivity, OnMapClickListener onMapClickListener, OnMarkerClickListener onMarkerClickListener, OnInfoWindowClickListener onInfoWindowClickListener) {
		this.showMapActivity = showMapActivity;
		this.onMapClickListener = onMapClickListener;
		this.onMarkerClickListener = onMarkerClickListener;
		this.onInfoWindowClickListener = onInfoWindowClickListener;

		SupportMapFragment mapFragment = (SupportMapFragment) activity.getChildFragmentManager()
				.findFragmentById(R.id.mapviewvdos);
		mapFragment.getMapAsync(this);



	}

	public Marker addMarker(LatLng pos, String title, String snippet, int icon) {
		return map.addMarker(new MarkerOptions().position(pos)
				.title("" + title).snippet("" + snippet)
				.icon(BitmapDescriptorFactory.fromResource(icon)));
	}

	public Marker addMarker(double latitude, double longitude, String title,
                            String snippet, int icon) {
		return map.addMarker(new MarkerOptions()

		.position(new LatLng(latitude, longitude)).title("" + title)
				.snippet("" + snippet)
				.icon(BitmapDescriptorFactory.fromResource(icon)));
	}

	/**
	 * Metodo que consulta la dirección.
	 *
	 * @param latitude
	 * @param longitude
	 */

	public static String ReadAddressFromWebService(String latitude,
                                                   String longitude) {

		String result = "";
		StringBuilder sb = new StringBuilder();
		sb.append("https://maps.googleapis.com/maps/api/geocode/json?latlng="
				+ latitude + "," + longitude + "&sensor=false");
		String url = sb.toString();
		HttpClient httpClient = new DefaultHttpClient();

		StringBuilder responseData = new StringBuilder();
		try {
			HttpResponse response = httpClient.execute(new HttpGet(url));
			response.addHeader("Accept-Language", "en-US");
			HttpEntity entity = response.getEntity();

			BufferedReader bf = new BufferedReader(new InputStreamReader(
					(entity.getContent()), "UTF-8"));
			String line = "";

			while ((line = bf.readLine()) != null)
				responseData.append(line);

			JSONObject jsonObj = new JSONObject(responseData.toString());

			JSONArray resultArry = jsonObj.getJSONArray("results");

			result = resultArry.getJSONObject(0).getString("formatted_address")
					.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * El metodo vista3D realiza la funcionalidad del evento lanzado por el
	 * botÓn 'Animar3D'.
	 *
	 *            - View
	 */
	public void vista3D(Context cxt, double latitud, double longitud) {

		try {
			LatLng coordenada = new LatLng(latitud, longitud);

			CameraPosition camPos = new CameraPosition.Builder()
					.target(coordenada).zoom(15).bearing(0).tilt(50).build();

			CameraUpdate camUpd3 = CameraUpdateFactory
					.newCameraPosition(camPos);

			map.animateCamera(camUpd3);

		} catch (IllegalArgumentException e) {
			Toast.makeText(cxt, "Coordenadas no válidas", Toast.LENGTH_SHORT)
					.show();

		}
	}

	public void hideFunctionsMap() {

		Visibility.gone(showMapActivity.viewLimpiar);
		//Visibility.gone(showMapActivity.container_steps);
		if(showMapActivity.container_steps.getVisibility() != View.GONE)
			AnimationEnrutate.translateUp(showMapActivity.container_steps);

		Visibility.gone(showMapActivity.limpiarMapTodo);
	}

	public void showFunctionsMap() {
		//Visibility.visible(showMapActivity.viewMapa);
		Visibility.visible(showMapActivity.viewLimpiar);
		Visibility.visible(showMapActivity.limpiarMapTodo);
		Visibility.visible(showMapActivity.container_steps);
		AnimationEnrutate.translateDown(showMapActivity.container_steps);

	}

	public void limpiarMapa() {

		showMapActivity.mode = "start";

		if (showMapActivity.travelSelected.listaRutas != null)
			showMapActivity.travelSelected.listaRutas.clear();

		if (showMapActivity.listaRutas != null)
			showMapActivity.listaRutas.clear();

		if (showMapActivity.inicioRecorrido != null)
			showMapActivity.inicioRecorrido.remove();

		if (showMapActivity.lineas1 != null)
			showMapActivity.lineas1.remove();

		if (showMapActivity.finRecorrido != null)
			showMapActivity.finRecorrido.remove();

		if (showMapActivity.lineas2 != null)
			showMapActivity.lineas2.remove();

		if (showMapActivity.lineas != null)
			showMapActivity.lineas.remove();

		if (showMapActivity.marcaGoogle != null)
			showMapActivity.marcaGoogle.remove();

		if (showMapActivity.travelSelected != null) {
			showMapActivity.travelSelected.clearSections();
			showMapActivity.travelSelected.drawed = false;
		}
		if (showMapActivity.imHere != null)
			showMapActivity.imHere.remove();

		if (showMapActivity.imGoingTo != null)
			showMapActivity.imGoingTo.remove();

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(showMapActivity.getActivity());
		boolean isDark = pref.getBoolean("Name", false);
		if (isDark) {
			if (!showMapActivity.travelSelected.isDrawed()) {
				if (showMapActivity.mapGoogle.map != null)
					showMapActivity.mapGoogle.map.clear();

			}
		} else {
			if (showMapActivity.travelSelected != null) {
				if (!showMapActivity.travelSelected.isDrawed()) {
					if (showMapActivity.mapGoogle.map != null)
						showMapActivity.mapGoogle.map.clear();

				}
			}

			showMapActivity.loadStations();

		}
		if (showMapActivity.marcasParadasCercanas != null
				&& showMapActivity.marcasParadasCercanas.size() > 0) {
			for (int i = 0, len = showMapActivity.marcasParadasCercanas.size(); i < len; ++i) {
				Marker tmp = showMapActivity.marcasParadasCercanas.get(i);
				tmp.remove();
			}
			showMapActivity.marcasParadasCercanas.clear();
		}
		// reestablece los footers
		showMapActivity.resetFooter();

		// quitar botones para limpiar y visualizar recorrido
		showMapActivity.mapGoogle.hideFunctionsMap();

	}

	public void dropPinEffect(final Marker marker) {
		// Handler allows us to repeat a code block after a specified delay
		final android.os.Handler handler = new android.os.Handler();
		final long start = SystemClock.uptimeMillis();
		final long duration = 1000;

		// Use the bounce interpolator
		final android.view.animation.Interpolator interpolator = new BounceInterpolator();

		// Animate marker with a bounce updating its position every 15ms
		handler.post(new Runnable() {
			@Override
			public void run() {

				long elapsed = SystemClock.uptimeMillis() - start;
				// Calculate t for bounce based on elapsed time
				float t = Math.max(
						1 - interpolator.getInterpolation((float) elapsed
								/ duration), 0);
				// Set the anchor
				marker.setAnchor(0.5f, 1.0f + 14 * t);

				if (t > 0.0) {
					// Post this event again 15ms from now.
					handler.postDelayed(this, 26);
				}
			}
		});
	}

	/**
	 * Distance in kilometers between two geo points.
	 *
	 * Example:
	 *
	 * double lat1 = -34.87001758635342; double lon1 = -56.16755962371826;
	 * double lat2 = -34.88487484011935; double lon2 = -56.1661434173584;
	 *
	 * double distance = distFrom(lat1, lon1, lat2, lon2);
	 *
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public double distFrom(double lat1, double lng1, double lat2,
			double lng2) {
		// double earthRadius = 3958.75;//miles
		// double earthRadius = 6371;//kilometers
		double earthRadius = 6371000; // meters
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist; // conversion a metros
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		if (map != null) {
			map.setOnMapClickListener(onMapClickListener);
			map.setOnMarkerClickListener(onMarkerClickListener);
			map.setOnInfoWindowClickListener(onInfoWindowClickListener);
			map.getUiSettings().setZoomControlsEnabled(false);

			map.getUiSettings().setAllGesturesEnabled(true);
			map.getUiSettings().setZoomGesturesEnabled(true);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			map.setMyLocationEnabled(true);

			LatLng cali = new LatLng(3.423507, -76.519046);

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(cali, ZOOM_LEVEL));
		}
	}
}
