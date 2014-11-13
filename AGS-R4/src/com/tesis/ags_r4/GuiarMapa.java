package com.tesis.ags_r4;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tesis.ags_r4.R;
import com.tesis.ags_r4.R.id;
import com.tesis.ags_r4.R.layout;
import com.tesis.ags_r4.file.MakeFile;
import com.tesis.ags_r4.location.MyLocation;
import com.tesis.ags_r4.location.MyLocationListener;
import com.tesis.ags_r4.navigation.GMapV2Direction;
import com.tesis.ags_r4.navigation.GetDirectionsAsyncTask;
import com.tesis.ags_r4.navigation.Instructions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;



public class GuiarMapa extends FragmentActivity {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
	/** Referencia al TAG de log. */
	private static final String TAG = "[DirectoAndroidV2_EJ2]";
	
    private GoogleMap mMap;
    private LocationManager locManager;
    private MyLocationListener locListener;
    private double lat, mlat;
    private double lng, mlng,latLineStopDest,lngLineStopDest,latLineStopMy,lngLineStopMy;
    private MyLocation mloc;
    /** Nombre del proveedor de localización. */
	private transient String proveedor;
	private static float[] results=new float[2];
	private MakeFile mfile=new MakeFile();
	private  ArrayList<Instructions> inst=new ArrayList<Instructions>();

    //Conectar con el gps y obtener ubicacion.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.guiar);
        lat=intent.getDoubleExtra("lat", 0.0);
		lng=intent.getDoubleExtra("lng", 0.0);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locListener = new MyLocationListener(){
			@Override
			public void onLocationChanged(Location loc) {//Cada vez que cambia mi posicion  le voy a informar que debe hacer..
				if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
					mlat=loc.getLatitude();
					mlng=loc.getLongitude();
					//cada vez que obtengo la mlat y mlong puedo ir calculando
					//la distancia que hay en donde estoy hasta donde quiero llegar.
					setLocation(loc);
					//cada vez que obtengo mi lat y lng voy cal dist hasta que este cerca del lugar e ir diciendo las instrucciones.
					//
				}
			}
		};	
    }
    
    

    @Override
    protected void onResume() {
        super.onResume();
        configGps();
        setUpMapIfNeeded();//tengo que obtener la lat y lng de entrada y hacer lo que hago
    }
    
    private void configGps(){
 		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0,(LocationListener) locListener);
 	  
		/*if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(getBaseContext(), "GPS DESACTIVADO", Toast.LENGTH_LONG)
            .show(); 
			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			this.startActivityForResult(settingsIntent, 0);
		}*/
		
		//Obtengo mi ubicacion y el punto destino que ya lo tengo, calculo la distancia 
        //para saber si guio hastta parada de colectivo o caminando
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
           // mMap.setMyLocationEnabled(true);
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
    	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.123873, -64.348993), 12));
    	//Construye la ruta, desde una latitud-longitud hasta otra latitud-longitud
    	//tengo que construir desde mi ubicacion actual hasta la parada de colectivo si se 
    	//superan ciertos metros, y sino hasta el lugar si esta cerca.
    	//findDirections(mlat,mlng,-33.123873,-64.348993, GMapV2Direction.MODE_WALKING );
    	ArrayList<String> listLine = new ArrayList<String>();
    	String coor;
    	for(int i=1;i<19;i++){
    		if (i==1 || i==2 || i==8 || i==9){
    			coor=mfile.recuperar(String.valueOf(i)+"r");
    			listLine.add(coor);
    			coor=mfile.recuperar(String.valueOf(i)+"v");
    			listLine.add(coor);
    		}else{
    			coor=mfile.recuperar(String.valueOf(i));
    			listLine.add(coor);		
    		}
    	}

    	if (this.dist(mlat, mlng,lat,lng)<1000){ //VER COMO OBTENER mlat y mlong solo una vez
    		//ruta hasta el lugar(destino) no lo llevo hasta la parada
    		findDirections(mlat,mlng,lat,lng, GMapV2Direction.MODE_WALKING );
    		//Agregar una var global para identificar si lo gui caminando o en bus.
    		//asi si va en bus le informo que debe bajarse y recalculo ruta, sino informo llegada a destino
    	}
    	else{ // sino hasta la parada de colectivo

    		int i=0;
    		double mCurrentDist;
    		double lngMy=0;
    		double latMy=0;
    		double lngDest=0;
    		double latDest = 0;
    		double currentDist;
    		double minDist=10000;
    		double minDistDest=10000;
    		String[] minCord=null;
    		int linea=99;
    		while (i<listLine.size()){
    			String[] listLatLng=(listLine.get(i)).split(",0");  
    			mCurrentDist=minDist( -64.337958,-33.121881,listLatLng,1);//mlat mlng mi ubicacion
    			currentDist=minDist(-64.345854,-33.124945,listLatLng,2);//lat lng la del lugar

    			if (currentDist<minDistDest && mCurrentDist<minDist){
    				minDistDest=currentDist;
    				minDist=mCurrentDist;
    				minCord=listLatLng;
    				//double latOr=-64.337958;//la de origen es mi ubcacion
    				//double lngOr=-33.121881;
    				lngMy=lngLineStopMy;//en latLineStopMy tengo la latitud a la parada mas cercana a mi ubicacion
    				latMy=latLineStopMy;
    				lngDest=lngLineStopDest;//en latLineStopDest tengo la latitud mas cercana a la parada del destino
    				latDest=latLineStopDest;//en latLineStop tengo la long mas cercana a la parada del destino
    			}
    			i++;
    		}

    		//cambia el orden de lat y long 1ero va lng y despues lat
    		findDirections(-33.121881,-64.337958,lngMy, latMy, GMapV2Direction.MODE_WALKING );
    		//findDirections(-33.124945,-64.345854,lngDest, latDest, GMapV2Direction.MODE_WALKING );//cuando se baja recalculo la ruta.
    		this.drawMap(minCord);
    		Toast.makeText(getBaseContext(), String.valueOf("Calculando Recorrido "), Toast.LENGTH_LONG)
    		.show();
    	}
    }
    
    public void findDirections(double fromPositionLat, double fromPositionLong, double toPositionLat, double toPositionLong, String mode)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
     
        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
       
        
        asyncTask.execute(map);
        
    }
    
    public void handleGetDirectionsResult(ArrayList directionPoints, ArrayList<Instructions> listInst)
    {
        Polyline newPolyline;
        GoogleMap mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add((LatLng) directionPoints.get(i));
        }
        newPolyline = mMap.addPolyline(rectLine);
        
       inst=listInst;
    }
    
    
    //cada vez que cambia de posicion se ejecuta este metodo
    // ir informando que instruccion debe realizar una vez que llega a destino pasar al paso de abajo.
    // y cuando este a 200m del lugar avisar q se baje(cuando esta arriba del lugar) informar calles
    public void setLocation(Location loc) {
    	Toast.makeText(getBaseContext(), "Mi Direccion es: "+loc.getLatitude(), Toast.LENGTH_LONG)
		.show();
    
    	
    	/*int latitud = (int) (loc.getLatitude() * 1E6);
		int longitud = (int) (loc.getLongitude() * 1E6);

		LatLng latLng = new LatLng(latitud, longitud);
		mMap.addMarker(new MarkerOptions().position(latLng).title(
				"Madrid, España"));
		//Obtener la direccin de la calle a partir de la latitud y la longitud 
		/*if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
			/*try {
				Geocoder geocoder = new Geocoder(this, Locale.getDefault());
				List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				if (!list.isEmpty()) {
					Address address = list.get(0);
				/*	messageTextView2.setText("Mi direccin es: \n"
							+ address.getAddressLine(0));*/
					
				//}

			/*} catch (IOException e) {
				e.printStackTrace();
			}*/
		//}
	}	
    /*
    public List getAddress(){
    	 Geocoder geocoder = new Geocoder(this);
         List<Address> addresses = null;
         //addresses.
         try {
             addresses = geocoder.getFromLocationName("COLOMBIA", 3);
         } catch (IOException e) {
             e.printStackTrace();
         }
         return addresses;
    }*/
    
    //Recorre la lista de direcciones y obtiene la lat y long
    /*for(int i=0;i<addresses.size();i++){
    	 
        Address address = (Address) addresses.get(i);

        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
    }*/       
    
    private void posicionInicial() {

		Criteria criteria = new Criteria();
		proveedor = locManager.getBestProvider(criteria, true);
		Location location = locManager.getLastKnownLocation(proveedor);
		if (null != location) {

			LatLng latLng = new LatLng(location.getLatitude(),
					location.getLongitude());
			mMap.addMarker(new MarkerOptions().position(latLng).title(
					"latitud=" + location.getLatitude() + " longitud="
							+ location.getLongitude()));
			Log.d(TAG, "Cargada la localización.");
		} else {
			Log.d(TAG, "Localizacion nula");
		}
	}
    
    private double dist(double mlat, double mlng,double lat, double lng){
    	//mloc=new MyLocation("GPS");
    	MyLocation.distanceBetween(mlat, mlng, lat, lng, results);
    	return results[0];
    }
    
    
    //CALCULAR LA DISTANCIA DE DONDE QUIERO IR Y COMPARAR LA LAT Y LNG
    //DEL DESTINO CON LOS ARCHIVOS DE LINEAS PARA VER CUAL ESTA MAS CERCA
    //Y CUAL MAS CERCA DEL ORIGEN TMB.(OJO CON TEMA IDA, VUELTA)
    //UNA VEZ LISTO ESTO, DIBUJAR RECORRIDO HASTA LA MEJOR OPCION(PARADA)
    //Y LUEGO AVISAR CUANDO SE ESTE CERCA DEL DESTINOO..
    //Hacer un Metodo que tome un arreglo de latitud y longitud
    //y dibuje el recorrido en el mapa.
    public void drawMap(String[] listLatLng){
    	//String coor=mfile.recuperar("17");
    	//String[] listLatLng=coor.split(",0");
    	int i=0;
    	double distCont=0.0;
    	Polyline newPolyline;
    	PolylineOptions line = new PolylineOptions().width(3).color(Color.BLUE);
    	while (i<listLatLng.length-1){
    		String[] ltln=listLatLng[i].split(",");
    		double lng=Double.parseDouble(ltln[0]);
    		double lat=Double.parseDouble(ltln[1]);
    		line.add(new LatLng(lat, lng));
    		i++;
    	}
    	this.drawStops(listLatLng);
    	//line.add(new LatLng(33.10933423127904,-64.30096289906105),new LatLng(-33.123873,-64.348993));
    	newPolyline = mMap.addPolyline(line);
    }
    
    
    //Dibujo las paradas estimativas de colectivo cada 200 metros.
    public void drawStops(String[] listLatLng ){
    	int i=0;
    	double distCont=0.0;
    	while (i<listLatLng.length-2){
    		String[] ltln=listLatLng[i].split(",");
    		double lng=Double.parseDouble(ltln[0]);
    		double lat=Double.parseDouble(ltln[1]);
    		double lngDest=Double.parseDouble(listLatLng[i+1].split(",")[0]);
    		double latDest=Double.parseDouble(listLatLng[i+1].split(",")[1]);
    		distCont=this.dist(lat, lng, latDest, lngDest)+distCont;
    		if(distCont>=200){
    			//poner parada
    			 mMap.addMarker(new MarkerOptions().position(new LatLng(latDest,lngDest)).title("Stop")
    					 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_bus_light)));
    			distCont=0.0;
    		}
    		i++;
    	}
    }
    
    //Minima distancia entre la parada de colectivo y mi ubicacion y la de destino
    public double minDist(double lat, double lng,String[] listLatLng,int m){
        int j=0;
        double minDist=10000;
    	while (j<listLatLng.length-1){
    		String[] ltln=listLatLng[j].split(",");
    		double latLine=Double.parseDouble(ltln[0]);
    		double lngLine=Double.parseDouble(ltln[1]);
    		double currentDist=this.dist(lat,lng, latLine, lngLine);
    		if (currentDist<minDist){
    			minDist=currentDist;
    			if(m==1){//Diferencio si es 1 para obtener la parada mas cercana a mi ubicacion y no a la del destino.
    				latLineStopMy=latLine;
    				lngLineStopMy=lngLine;
    			}else{
    				latLineStopDest=latLine;
    				lngLineStopDest=lngLine;
    			}
    		}
    		j++;
    	}
    	return minDist;
    }
    
    
    //Metodo que recalcula la ruta hasta el destino cuando se bajo del colectivo
    public void RecalRoute(){
    	
    }
    
    //Metodo que recorre la lista y agrega la palabra metros, pasos a la distancia,
    //Insrucciones borra lo que esta entre <>
    // pasaje de duracion en segundos a minutos dependiendo..
    public void fixListInst(){
    	
    }
 
    
}
