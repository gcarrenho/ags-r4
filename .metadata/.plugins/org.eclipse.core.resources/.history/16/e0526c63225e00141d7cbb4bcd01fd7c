package com.tesis.ags_r4.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.tesis.ags_r4.AgsIntents;
import com.tesis.ags_r4.R;
import com.tesis.ags_r4.R.id;
import com.tesis.ags_r4.R.layout;
import com.tesis.ags_r4.R.string;
import com.tesis.ags_r4.location.MyLocationListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;


public class MainMenuActivity extends Activity {

	public static final int APP_EXIT_CODE = 4;
	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";
	private int satellitesInFix=0;
	private int satellites=0;
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == APP_EXIT_CODE){
			getMyApplication().closeApplication(this);
		}
	}*/
	
	
	/*public static Animation getAnimation(int left, int top){
		Animation anim = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, left, 
				TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, top, TranslateAnimation.RELATIVE_TO_SELF, 0);
		anim.setDuration(700);
		anim.setInterpolator(new AccelerateInterpolator());
		return anim;
	}*/
	
	//Esto solamente para hacer la animacion
	public static void onCreateMainMenu(Window window, final Activity activity){
		View head = (View) window.findViewById(R.id.Headliner);
		//head.startAnimation(getAnimation(0, -1));
		
		View leftview = (View) window.findViewById(R.id.guiaButton);
		//leftview.startAnimation(getAnimation(-1, 0));
		leftview = (View) window.findViewById(R.id.favoritosButton);
		//leftview.startAnimation(getAnimation(-1, 0));
		
		View rightview = (View) window.findViewById(R.id.abmButton);
		//rightview.startAnimation(getAnimation(1, 0));
		rightview = (View) window.findViewById(R.id.informButton);
		//rightview.startAnimation(getAnimation(1, 0));
		
		/*final String textVersion = Version.getAppVersion(((OsmandApplication) activity.getApplication()));
		final TextView textVersionView = (TextView) window.findViewById(R.id.TextVersion);
		textVersionView.setText(textVersion);
		final SharedPreferences prefs = activity.getApplicationContext().getSharedPreferences("net.osmand.settings", MODE_WORLD_READABLE);
		textVersionView.setOnClickListener(new OnClickListener(){

			int i = 0;
			@Override
			public void onClick(View v) {
				if(i++ > 8) {
					prefs.edit().putBoolean(CONTRIBUTION_VERSION_FLAG, true).commit();
					enableLink(activity, textVersion, textVersionView);
				}
			}
		});*/
		/*if (prefs.contains(CONTRIBUTION_VERSION_FLAG)) {
			enableLink(activity, textVersion, textVersionView);
		}
		View helpButton = window.findViewById(R.id.HelpButton);
		helpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TipsAndTricksActivity tactivity = new TipsAndTricksActivity(activity);
				Dialog dlg = tactivity.getDialogToShowTips(false, true);
				dlg.show();
			}
		});*/
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//((OsmandApplication) getApplication()).applyTheme(this);
		super.onCreate(savedInstanceState);
		boolean exit = false;
		if(getIntent() != null){
			Intent intent = getIntent();
			if(intent.getExtras() != null && intent.getExtras().containsKey(APP_EXIT_KEY)){
				exit = true;
			}
		}
		//Aca vamos a tener que activar gps y encontrar satelites...
		//Tirar un cartel diciendo espere hasta que se localice su ubicacion.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		/*LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		 
		MyLocationListener locListener = new MyLocationListener();
		locListener.setMenuActivity(this);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,(LocationListener) locListener);*/

		//localizacion solo si psaron cinco segundo de la localizacion o se a movido mas de 10 metros de la ultima localizacion.
		// Comprobamos si está disponible el proveedor GPS.
		//Lo unico que necesito en este activity es que el gps este activado y se conecte con los satelites.
		/*if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(getBaseContext(), "GPS DESACTIVADO", Toast.LENGTH_LONG)
            .show(); 
			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			this.startActivityForResult(settingsIntent, 0);
		//mostrarAvisoGpsDeshabilitado(); Abrir ventana para que active gps
		}*/

		onCreateMainMenu(getWindow(), this);

		Window window = getWindow();
		final Activity activity = this;
		//ACA VAMOS A MODIFICAR PARA QUE DEL BOTON GUIAR NOS ABRA OTRA VISTA NO EL MAPA.
		/*View showMap = window.findViewById(R.id.guiaButton);
		showMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent mapIndent = new Intent(activity, OsmandIntents.getMapActivity());
				activity.startActivityForResult(mapIndent, 0);
			}
		});*/
		View guiarButton = window.findViewById(R.id.guiaButton);
		guiarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Ventana para seleccionar el lugar
				//una vez seleccionado, calcular la distancia desde mi ubicacion.
				// si la distancia no supera tantos metros(determinar cuantos) guiar caminando
				//sino identificar que garita de colectivo frena cerca yendo desde mi ubicacion. 
				//final Intent guiar = new Intent(activity, AgsIntents.getGuiarMapa());
				final Intent guiar = new Intent(activity, AgsIntents.getSelecCatActivity());
				guiar.putExtra("boton", "guiar");
				activity.startActivity(guiar);
			}
		});
		
		//Evento si se realiza una pulzacion prolongada en el boton
		guiarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				return true;
	         }
			
		});
		
		//Se presiono el boton abm
		View abmButton = window.findViewById(R.id.abmButton);
		abmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent abm = new Intent(activity, AgsIntents.getAbmActivity());
				activity.startActivity(abm);
			}
		});
		
		//Evento que se ejecuta si se realiza un click prolongado sobre el boton.
		abmButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	             return true;
	         }
			
		});

		//Se presiono el boton favoritos
		View favoritosButton = window.findViewById(R.id.favoritosButton);
		favoritosButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent favoritos = new Intent(activity, AgsIntents.getFavoritosActivity());
				favoritos.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				activity.startActivity(favoritos);
			}
		});

		//Evento que se ejecuta si se realiza un click prolongado sobre el boton
		favoritosButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	            return true;
	         }
			
		});
		
		
		View infButton = window.findViewById(R.id.informButton);
		infButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent inf = new Intent(activity, AgsIntents.getInfActivity());
				inf.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				activity.startActivity(inf);
			}
		});
		
		//Evento que se ejecuta si se realiza un click prolongado sobre el boton
		infButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	            return true;
	         }
			
		});
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
