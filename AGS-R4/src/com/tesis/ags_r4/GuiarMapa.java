package com.tesis.ags_r4;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tesis.ags_r4.R;
import com.tesis.ags_r4.location.Location;
import com.tesis.ags_r4.location.MyLocationListener;
import com.tesis.ags_r4.navigation.GMapV2Direction;
import com.tesis.ags_r4.navigation.GetDirectionsAsyncTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;



public class GuiarMapa extends FragmentActivity {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private Location loc=new Location("GPS");
    public static final PolylineOptions POLILINEA = new PolylineOptions()
	.add(new LatLng(-33.123873,-64.348993))
	.add(new LatLng(-33.112731,-64.309854));

    //Conectar con el gps y obtener ubicacion.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guiar);
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.getGpsStatus(null).getTimeToFirstFix();
		 
		MyLocationListener locListener = new MyLocationListener(); 
		locListener.setGuiarActivity(this);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,(LocationListener) locListener);

		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(getBaseContext(), "GPS DESACTIVADO", Toast.LENGTH_LONG)
            .show(); 
			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			this.startActivityForResult(settingsIntent, 0);
		}
		
		//Obtengo mi ubicacion y el punto destino que ya lo tengo, calculo la distancia 
        //para saber si guio hastta parada de colectivo o caminando
		 
		 if(loc.getDistance(-33.123873, -64.348993,-33.112731,-64.309854)<1000){
			 //Distancia corta, guiar hasta el lugar deseado;
		 }else{
			/**Distancia larga, Hacer:
			 *Encontrar paradas cercanas al destino
			 *Encontrar paradas cercanas a mi ubicacion, guiar hasta dicha parada.
			 *Luego al descender del colectivo guiar hasta el destino.*/
			 setUpMapIfNeeded();
		 }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * Ponemos como marcador mi ubicacion y a donde deseo dirigirme, calculo la distancia.
     * 
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() { 
    	mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.addMarker(new MarkerOptions().position(new LatLng(-33.123873,-64.348993)).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.123873, -64.348993), 12));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-33.112731,-64.309854)).title("Marker")); 
        //POLILINEA.color(Color.RED);
        //POLILINEA.width(5);
        //mMap.addPolyline(POLILINEA);
        
        //Construye la ruta, desde una latitud-longitud hasta otra latitud-longitud
        //tengo que construir desde mi ubicacion actual hasta la parada de colectivo si se 
        //superan ciertos metros, y sino hasta el lugar si esta cerca.
        findDirections(-33.123873,-64.348993,-33.112731, -64.309854, GMapV2Direction.MODE_WALKING );
        Toast.makeText(getBaseContext(), String.valueOf("Calculando Recorrido"), Toast.LENGTH_LONG)
        .show();
    }
    
    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
     
        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
    }
    
    public void handleGetDirectionsResult(ArrayList directionPoints)
    {
        Polyline newPolyline;
        GoogleMap mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.BLUE);
        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add((LatLng) directionPoints.get(i));
        }
        newPolyline = mMap.addPolyline(rectLine);
    }
    
    
    public void setLocation(Location loc) {
		//Obtener la direccin de la calle a partir de la latitud y la longitud 
		if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
			try {
				Geocoder geocoder = new Geocoder(this, Locale.getDefault());
				List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				if (!list.isEmpty()) {
					Address address = list.get(0);
				/*	messageTextView2.setText("Mi direccin es: \n"
							+ address.getAddressLine(0));*/
					Toast.makeText(getBaseContext(), "Mi Direccion es: "+address.getAddressLine(0), Toast.LENGTH_LONG)
					.show();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
}
