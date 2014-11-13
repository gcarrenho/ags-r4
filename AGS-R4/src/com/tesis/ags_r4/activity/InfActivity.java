package com.tesis.ags_r4.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tesis.ags_r4.R;
import com.tesis.ags_r4.file.MakeFile;
import com.tesis.ags_r4.navigation.GMapV2Direction;
import com.tesis.ags_r4.navigation.GetDirectionBusAsyncTask;
import com.tesis.ags_r4.navigation.GetDirectionsAsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class InfActivity extends Activity{

	private MakeFile mfile=new MakeFile();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean exit = false;
		Intent intent = getIntent();
		/*if(intent != null){
			if(intent.getExtras() != null && intent.getExtras().containsKey(APP_EXIT_KEY)){
				exit = true;
			}
		}*/
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.informacion);
		Window window = getWindow();
		View actButton = window.findViewById(R.id.button_act);
		//Evento que escucha el click sobre el boton cancelar
		actButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findDirections(0.2563,0.25369,0.89658,0.7895,"walking");
				Toast.makeText(getBaseContext(), "Los datos fueron grabados correctamente",
			            Toast.LENGTH_SHORT).show();
			}
		});
		
		View recButton = window.findViewById(R.id.button_rec);
		//Evento que escucha el click sobre el boton recuperar
		recButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String coord=mfile.recuperar("17");
				String[] listLatLng=coord.split(",0");
				EditText text=(EditText)findViewById(R.id.editText1);
				text.setText(String.valueOf(listLatLng.length));
			}
		});
		
		View button3 = window.findViewById(R.id.button3);
		//Evento que escucha el click sobre el boton recuperar
		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//String coord=mfile.recuperar("17");
				//String[] listLatLng=coord.split(",0");
				findDirections(-33.121881,-64.337958,-33.124945,-64.345854, GMapV2Direction.MODE_WALKING );
				//findDirections(0.2563,0.25369,0.89658,0.7895,"walking");
				/*EditText text=(EditText)findViewById(R.id.editText1);
				text.setText(String.valueOf(text));*/
			}
		});
	}
	    
	    public void findDirections(double fromPositionLat, double fromPositionLong, double toPositionLat, double toPositionLong, String mode)
	    {
	        Map<String, String> map = new HashMap<String, String>();
	        map.put(GetDirectionBusAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionLat));
	        map.put(GetDirectionBusAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionLong));
	        map.put(GetDirectionBusAsyncTask.DESTINATION_LAT, String.valueOf(toPositionLat));
	        map.put(GetDirectionBusAsyncTask.DESTINATION_LONG, String.valueOf(toPositionLong));
	        map.put(GetDirectionBusAsyncTask.DIRECTIONS_MODE, mode);
	     
	        GetDirectionBusAsyncTask asyncTask = new GetDirectionBusAsyncTask(this);
	        asyncTask.execute(map);
	    	/* Map<String, String> map = new HashMap<String, String>();
	         map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionLat));
	         map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionLong));
	         map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionLat));
	         map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionLong));
	         map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
	      
	         GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
	         asyncTask.execute(map);*/
	         
	    }
	    
	    public void handleGetDirectionsResult(ArrayList directionPoints)
	    {
	       // Polyline newPolyline;
	        //GoogleMap mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	        //PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
	        for(int i = 0 ; i < directionPoints.size() ; i++)
	        {
	            //rectLine.add((LatLng) directionPoints.get(i));
	        }
	        Toast.makeText(getBaseContext(), "Mi Direccion es: "+directionPoints.get(0), Toast.LENGTH_LONG)
			.show();
	        //newPolyline = mMap.addPolyline(rectLine);
	        
	        EditText text=(EditText)findViewById(R.id.editText1);
			text.setText(String.valueOf(directionPoints.get(2)));
	    }
}
