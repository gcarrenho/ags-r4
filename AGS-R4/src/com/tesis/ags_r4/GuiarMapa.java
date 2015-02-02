package com.tesis.ags_r4;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import com.google.android.gms.maps.model.Marker;
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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;



public class GuiarMapa extends FragmentActivity /*implements SensorEventListener*/ {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
	/** Referencia al TAG de log. */
	private static final String TAG = "[DirectoAndroidV2_EJ2]";
	
    private GoogleMap mMap;
    private LocationManager locManager;
    private MyLocationListener locListener;
    private double lat, mlat, lngDest=0, latDest = 0;
    private double lng, mlng,latLineStopDest,lngLineStopDest,latLineStopMy,lngLineStopMy;
    private MyLocation mloc;
    /** Nombre del proveedor de localización. */
	private transient String proveedor;
	private static float[] results=new float[2];
	private MakeFile mfile=new MakeFile();
	private  ArrayList<Instructions> inst=new ArrayList<Instructions>();
	private String textInst=new String();
	private Marker marker;
	private int acces, stop=0, llegoDest=0;
	private int l=1;
	//private SensorManager mSensorManager;
	static final int sensor = SensorManager.SENSOR_ORIENTATION;
	 protected PowerManager.WakeLock wakelock;
	 
    //Conectar con el gps y obtener ubicacion.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.guiar);
        lat=intent.getDoubleExtra("lat", 0.0);
		lng=intent.getDoubleExtra("lng", 0.0);
		// Creamos el objeto para acceder al servicio de sensores CUANDO TENGA UN MOVIL CON DICHO SENSOR PODRE PROBAR.
		/*SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		List<Sensor> listaSensores = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		listaSensores = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if (!listaSensores.isEmpty()) {
		       Sensor orientationSensor = listaSensores.get(0);
		       sensorManager.registerListener(this, orientationSensor,SensorManager.SENSOR_DELAY_FASTEST);
		}else{
		 Toast.makeText(getBaseContext(), "No hay sensores", Toast.LENGTH_LONG)
    		.show();
		}*/
		    		
		final PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();
        
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locListener = new MyLocationListener(){
			@Override
			public void onLocationChanged(Location loc) {//Cada vez que cambia mi posicion  le voy a informar que debe hacer..
				if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
					mlat=loc.getLatitude();
					mlng=loc.getLongitude();
					//Cuando obtenga la posicion dibujo la ruta. y Digo el recorrido a hacer.
					//una vez que ya la dibuje, cada vez que obtenga la posicion hago  otra cosa.
					if(acces==0){
						setUpMap();
						acces=1;
					}else{
					/*Toast.makeText(getBaseContext(), String.valueOf("Latitud "+ mlat +" Longitud "+mlng), Toast.LENGTH_LONG)
		    		.show();*/
				/*	marker.remove();
					marker=mMap.addMarker(new MarkerOptions().position(new LatLng(mlat,mlng))
	    					 .icon(BitmapDescriptorFactory.fromResource(R.drawable.pedestrian_bearing)));*/
					setLocation(loc);}
					
				}
			}
		};
    }
    
    
    //Metodo para para de escuchar los sensores(deberia hacerse en destroy, pause, y cuando finaliza el recorrido)
    //mSensorManager.unregisterListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));

    @Override
    protected void onResume() {
        super.onResume();
        configGps();
        setUpMapIfNeeded();//tengo que obtener la lat y lng de entrada y hacer lo que hago
    }
     
    
    @Override
    protected void onStop(){
        super.onStop();
        locManager.removeUpdates(locListener);
        this.wakelock.release();
        finish();
    }
      
    private void configGps(){
 		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 40000, 0,(LocationListener) locListener);
 	  
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
                //setUpMap();//borrar esto una vez que ya se probo, tiene que hacerse solo cuando se conecta el gps
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
    	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mlat,mlng), 12));
    	//Construye la ruta, desde una latitud-longitud hasta otra latitud-longitud
    	//tengo que construir desde mi ubicacion actual hasta la parada de colectivo si se 
    	//superan ciertos metros, y sino hasta el lugar si esta cerca.
    	
    	if(this.dist(mlat,mlng, lat,lng)<=1000){
    		//Recorrido desde donde estoy hasta el lugar
    		findDirections(mlat,mlng,lat,lng, GMapV2Direction.MODE_WALKING );
    	}else{
		    	ArrayList<Pair> listLine = new ArrayList<Pair>();
		    	String coor;
		    	for(int i=1;i<19;i++){
		    		if (i==1 || i==2 || i==8 || i==9){
		    			coor=mfile.recuperar(String.valueOf(i)+"r");
		    			Pair linCor=new Pair(coor,String.valueOf(i)+"r");
		    			listLine.add(linCor);
		    			coor=mfile.recuperar(String.valueOf(i)+"v");
		    			linCor=new Pair(coor,String.valueOf(i)+"v");
		    			listLine.add(linCor);
		    		}else{
		    			coor=mfile.recuperar(String.valueOf(i));
		    			Pair linCor=new Pair(coor,String.valueOf(i));
		    			listLine.add(linCor);	
		    		}
		    	}
		
		    		int i=0;
		    		double mCurrentDist;
		    		double lngMy=0;
		    		double latMy=0;
		    		double currentDist;
		    		double minDist=10000;
		    		double minDistDest=10000;
		    		String[] minCord=null;
		    		int linea=0;
		    		while (i<listLine.size()){
		    			//-33.123507, 
		    			String[] listLatLng=String.valueOf((listLine.get(i)).first).split(",0");  
		    			mCurrentDist=minDist(mlat,mlng ,listLatLng,1);//mlat mlng mi ubicacion
		    			currentDist=minDist(lat, lng,listLatLng,2);//lat lng la del lugar
		
		    			if (currentDist<minDistDest && mCurrentDist<minDist){
		    				minDistDest=currentDist;
		    				minDist=mCurrentDist;
		    				minCord=listLatLng;
		    				linea=i; //Aca tengo la linea de colectivo que debera tomarse
		    				//double latOr=-64.337958;//la de origen es mi ubcacion
		    				//double lngOr=-33.121881;
		    				lngMy=lngLineStopMy;//en latLineStopMy tengo la latitud a la parada mas cercana a mi ubicacion
		    				latMy=latLineStopMy;
		    				lngDest=lngLineStopDest;//en latLineStopDest tengo la latitud mas cercana a la parada del destino
		    				latDest=latLineStopDest;//en latLineStop tengo la long mas cercana a la parada del destino
		    			}
		    			i++;
		    		}
		    		
		    		 mMap.addMarker(new MarkerOptions()
		    	        .position(new LatLng(lat, lng))
		    	        .title("Destino"));
		    		//-33.112120, -64.308465 Barrio;
		    		//-33.107446, -64.321168
		    		findDirections(mlat,mlng ,latMy, lngMy, GMapV2Direction.MODE_WALKING );
		    		//findDirections(-33.124945,-64.345854,lngDest, latDest, GMapV2Direction.MODE_WALKING );//cuando se baja recalculo la ruta.
		    		this.drawMap(minCord);
		    		Toast.makeText(getBaseContext(), R.string.calc_rec, Toast.LENGTH_LONG)
		    		.show();
		    		Toast.makeText(getBaseContext(), "Linea a Tomar, "+String.valueOf((listLine.get(linea)).second), Toast.LENGTH_LONG)
		    		.show();
    			}
    	}
    //}
    
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
       fixListInst(inst);
		Toast.makeText(getBaseContext(),textInst, Toast.LENGTH_LONG)
		.show();
    }
    
    
    //cada vez que cambia de posicion se ejecuta este metodo
    // ir informando que instruccion debe realizar una vez que llega a destino pasar al paso de abajo.
    // y cuando este a 200m del lugar avisar q se baje(cuando esta arriba del lugar) informar calles
    public void setLocation(Location loc) {
   //ARREGLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR ACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
	    	if (inst.size()>l){
		    	if (this.dist(loc.getLatitude(), loc.getLongitude(),Double.parseDouble(inst.get(l).getLat()),Double.parseDouble(inst.get(l).getLng()))<=20){
		    		//digo que hay que hacer
		    		Toast.makeText(getBaseContext(), inst.get(l).getInstruction(), Toast.LENGTH_LONG)
		    		.show();
		    		//y a lat y lng le doy el siguiente de la lista
		    		l++;
		    	}
		    	else{//le digo donde esta parado
		    		int latitud = (int) (loc.getLatitude() * 1E6);
		    		int longitud = (int) (loc.getLongitude() * 1E6);
		    		
		    		//Obtener la direccion de la calle a partir de la latitud y la longitud 
		    		if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
		    			try {
		    				Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		    				List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
		    				if (!list.isEmpty()) {
		    					Address address = list.get(0);
	
		    					Toast.makeText(getBaseContext(),"Usted se Encuentra en "+address.getAddressLine(0), Toast.LENGTH_LONG)
		    		    		.show();
		    				}
		
		    			} catch (IOException e) {
		    				e.printStackTrace();
		    			}
		    		}
		    	}
	    	}else if(inst.size()==l && stop==0){
	    		Toast.makeText(getBaseContext(),"Se Encuentra Muy Proximo a la parada de colectivo.", Toast.LENGTH_LONG)
	    		.show();
	    		stop=1;
	    	}else if(inst.size()==l && stop==1){
	    		if(this.dist(loc.getLatitude(), loc.getLongitude(),latDest,lngDest)>60 && this.dist(loc.getLatitude(), loc.getLongitude(),latDest,lngDest)<=200){
	    			Toast.makeText(getBaseContext(),"Deberia Tocar el Timbre para descender del Transporte", Toast.LENGTH_LONG)
		    		.show();
	    		}else if (this.dist(loc.getLatitude(), loc.getLongitude(),latDest,lngDest)<=60){
	    			//recalcular la ruta
	    			//pensar cuando recalculo, la lista se actualiza o se incrementa en tamano de intruccions
	    			//las variables hay que reiniciarlas..
	    			findDirections(lat,lng,lngDest, latDest, GMapV2Direction.MODE_WALKING );
	    			llegoDest=1;
	    			l=1;
	    		}else{
	    			//podemos informar donde esta.. pero esto lo vemos..
	    			int latitud = (int) (loc.getLatitude() * 1E6);
		    		int longitud = (int) (loc.getLongitude() * 1E6);
		    		
		    		//Obtener la direccion de la calle a partir de la latitud y la longitud 
		    		if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
		    			try {
		    				Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		    				List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
		    				if (!list.isEmpty()) {
		    					Address address = list.get(0);
	
		    					Toast.makeText(getBaseContext(),"Usted se Encuentra en "+address.getAddressLine(0), Toast.LENGTH_LONG)
		    		    		.show();
		    				}
		
		    			} catch (IOException e) {
		    				e.printStackTrace();
		    			}
		    		}
	    		}
	    	}else if(inst.size()==l && stop==1 && llegoDest==1){
	    		if (this.dist(loc.getLatitude(), loc.getLongitude(),lat,lng)<=30){
	    			Toast.makeText(getBaseContext(),"Se Encuentra Muy Proximo al destino deseado. Recorrido Finalizado.", Toast.LENGTH_LONG)
		    		.show();
	    			//Apagar gps o volver al menu.
	    		}
	    		
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
    		double latLine=Double.parseDouble(ltln[1]);
    		double lngLine=Double.parseDouble(ltln[0]);
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
    //Insrucciones borra lo que esta entre <....>
    // pasaje de duracion en segundos a minutos dependiendo..
    public void fixListInst(ArrayList<Instructions> inst){
    	int i=0; 
    	while(i<inst.size()){
    		if(Integer.parseInt(inst.get(i).getDistance())<1000){
    			inst.get(i).setDistance(inst.get(i).getDistance()+" Metros, o "+Math.round(Integer.parseInt(inst.get(i).getDistance())/0.6) +" Pasos ");
    		}else{
    			inst.get(i).setDistance(Math.round(Integer.parseInt(inst.get(i).getDistance())/1000)+" Kilometros, o "+Math.round(Integer.parseInt(inst.get(i).getDistance())/1000/0.6) +" Pasos ");
    		}
    		int j=0;
			String instruc=inst.get(i).getInstruction();
			String newInstr=new String();
			while(j<instruc.length()){
				if(instruc.charAt(j)=='<'){
					while(instruc.charAt(j)!='>'){
						j++;
					}
					j++;
				}else{
					newInstr=newInstr+instruc.charAt(j);	
					j++;
				}
			}
			inst.get(i).setInstruction(newInstr);
			textInst=textInst+".\n"+inst.get(i).getInstruction()+" a "+inst.get(i).getDistance();
			i++;
    	}
    }

    
    
    //Si se vuelve atras o el telefono se suspende el gps se apaga.
    //Al ciego se le va a suspender muy seguido...
    /*@Override
    protected void onStop() {
        super.onStop();
        locManager.removeUpdates(locListener);   
    }*/
    
    



    
  //HASTA QUE NO CONSIGA UN TELEFONO CON SENSOR DE ORIENTACION, NO VOY A PODER PROBAR DICHO SENSOR  
 /*
	private String getDireccion(float values) {
		String txtDirection = "";
		if (values < 22)
			txtDirection = "Norte";
		else if (values >= 22 && values < 67)
			txtDirection = "Noreste";
		else if (values >= 67 && values < 112)
			txtDirection = "Este";
		else if (values >= 112 && values < 157)
			txtDirection = "Sureste";
		else if (values >= 157 && values < 202)
			txtDirection = "Sur";
		else if (values >= 202 && values < 247)
			txtDirection = "Suroeste";
		else if (values >= 247 && values < 292)
			txtDirection = "Oeste";
		else if (values >= 292 && values < 337)
			txtDirection = "Noroeste";
		else if (values >= 337)
			txtDirection = "Norte";

		return txtDirection;
	}*/	
	 // Metodo que escucha el cambio de los sensores
		//@Override
		/*public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			String txt = "\n\nSensor: ";
			Toast.makeText(getBaseContext(),"Escuchandoooo", Toast.LENGTH_LONG)
			.show();
			synchronized (this) {
				Log.d("sensor", event.sensor.getName());

				//if (event.sensor.getType()==Sensor.TYPE_ORIENTATION) {
				
					txt += "orientation\n";
					txt += "\n azimut: " + getDireccion(event.values[0]);
					txt += "\n y: " + event.values[1] + "Œ";
					txt += "\n z: " + event.values[2] + "Œ";
					//orientacion.setText(txt);
					Toast.makeText(getBaseContext(),"Va con Direcciona al "+ getDireccion(event.values[0]), Toast.LENGTH_LONG)
		    		.show();
				//}
			}	
			
		}



		// Metodo que escucha el cambio de sensibilidad de los sensores	
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}*/
	
}
