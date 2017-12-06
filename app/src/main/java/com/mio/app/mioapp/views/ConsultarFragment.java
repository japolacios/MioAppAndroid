package com.mio.app.mioapp.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import com.mio.app.mioapp.R;
import com.mio.app.mioapp.model.ConexionHTTP;
import com.mio.app.mioapp.model.PuntoRecarga;
import com.mio.app.mioapp.model.Rutas;
import com.mio.app.mioapp.model.Seccion;
import com.mio.app.mioapp.model.Vehiculo;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;

public class ConsultarFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Marker actual;

    private static double latitud;
    private static double longitud;
    private ConexionHTTP con;
    private Rutas rutas;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rutas = new Rutas(getActivity());

        latitud = 0;
        longitud = 0;

        View view = inflater.inflate(R.layout.fragment_consultar, container, false);

        if(view!=null) {

            String[] str = new String[rutas.getLista().size()];
            int i = 0;
            for (Map.Entry<String, String> entry : rutas.getLista().entrySet()) {
                str[i] = entry.getKey();
                i++;
            }

            final AutoCompleteTextView ruta = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, str);
            ruta.setAdapter(adapter);


            final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            miUbucacion();

            ImageButton imageButton = (ImageButton) view.findViewById(R.id.imageButton9);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable()) {

                        final String nombreRut = ruta.getText().toString().toUpperCase();
                        String id = rutas.obtenerNombreRuta(nombreRut);

                        if(id!=null) {
                            con = new ConexionHTTP("http://190.216.202.35:90/gtfs/realtime/", id, nombreRut);

                            try {
                                while (!con.isTerminoProceso()) {
                                    Toast.makeText(getContext(), "CARGANDO", Toast.LENGTH_SHORT).show();
                                    Thread.sleep(500);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            onMapReady(mMap);
                        }else{
                            Toast.makeText(getContext(), "INGRESE UNA RUTA CORRECTA", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "CONECTE A INTERNET", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
        return view;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager conexion = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active = conexion.getActiveNetworkInfo();
        return active!=null && active.isConnected();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        miUbucacion();

        if(mMap!=null && con!=null) {

            ArrayList<Vehiculo> d = con.getVehiculos();

            if(d.size()==0){
                Toast.makeText(getContext(), "NO HAY RUTAS CERCANAS", Toast.LENGTH_SHORT).show();
            }

            for (int i = 0; i < d.size(); i++) {
                if(esCercana(latitud,longitud,d.get(i).getLatitud(), d.get(i).getLongitud())) {
                    Toast.makeText(getContext(), "Vehiculo " + i, Toast.LENGTH_SHORT).show();
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(d.get(i).getLatitud(), d.get(i).getLongitud()))
                            .title(d.get(i).getNombreVehiculo()));
                }
            }
        }
    }

    private boolean esCercana(double y1, double x1, double y2, double x2){
        boolean loes = false;

        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(y2 - y1);
        double dLng = Math.toRadians(x2 - x1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(y1)) * Math.cos(Math.toRadians(y2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = radioTierra * va2;

        return distancia<3;
    }


    private void miUbucacion() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,locListener);

    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void actualizarUbicacion(Location locacion) {
        if(getContext()!=null){
            Toast.makeText(getContext(), "Actualiza", Toast.LENGTH_SHORT).show();
            if (locacion != null) {
                latitud = locacion.getLatitude();
                longitud = locacion.getLongitude();
                agregarActual(latitud, longitud);
            }
        }
    }


    private void agregarActual(double lat, double lng) {
        if (getContext() != null && mMap!=null) {
            LatLng coordenadas = new LatLng(lat, lng);
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
            if (actual != null) actual.remove();
            actual = mMap.addMarker(new MarkerOptions()
                    .position(coordenadas)
                    .title("Tu posición actual"));
            mMap.animateCamera(miUbicacion);
        }
    }

}