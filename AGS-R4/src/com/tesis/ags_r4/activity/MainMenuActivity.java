package com.tesis.ags_r4.activity;

import com.tesis.ags_r4.AgsIntents;
import com.tesis.ags_r4.R;
import com.tesis.ags_r4.R.id;
import com.tesis.ags_r4.R.layout;
import com.tesis.ags_r4.R.string;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;


public class MainMenuActivity extends Activity {

	public static final int APP_EXIT_CODE = 4;
	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";
	
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		// Comprobamos si está disponible el proveedor GPS.
		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(getBaseContext(), "GPS APAGADO", Toast.LENGTH_LONG)
            .show(); 
		//mostrarAvisoGpsDeshabilitado();
		}
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
				Toast.makeText(getBaseContext(), R.string.guiar_Button, Toast.LENGTH_LONG)
	            .show(); 
			}
		});
		
		guiarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	            //Abrir seleccionar entre Caminar o Bus
	            return true;
	         }
			
		});
		
		//Se presiono el boton abm
		View abmButton = window.findViewById(R.id.abmButton);
		abmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//final Intent abm = new Intent(activity, AgsIntents.getAbmActivity());
				//activity.startActivity(abm);
				Toast.makeText(getBaseContext(), R.string.abm_button, Toast.LENGTH_LONG)
	            .show();
			}
		});
		
		abmButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	             // Perform action on click
				final Intent abm = new Intent(activity, AgsIntents.getAbmActivity());
				activity.startActivity(abm);
	            return true;
	         }
			
		});

		//Se presiono el boton favoritos
		View favoritosButton = window.findViewById(R.id.favoritosButton);
		favoritosButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), R.string.favoritos_Button, Toast.LENGTH_LONG)
	            .show();
			}
		});

		favoritosButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	             // Perform action on click
				final Intent favoritos = new Intent(activity, AgsIntents.getFavoritosActivity());
				favoritos.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				activity.startActivity(favoritos);
	            return true;
	         }
			
		});
		//final View closeButton = window.findViewById(R.id.CloseButton);
		/*closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//cerrar la aplicacion si hace click pero dos click al primero no hacer nada
				//getMyApplication().closeApplication(activity);
			}
		});*/
		View infButton = window.findViewById(R.id.informButton);
		infButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), R.string.information_Button, Toast.LENGTH_LONG)
	            .show();
			}
		});
		
		infButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	             // Perform action on click
				final Intent inf = new Intent(activity, AgsIntents.getInfActivity());
				inf.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				activity.startActivity(inf);
	            return true;
	         }
			
		});
		/*
		if(exit){
			getMyApplication().closeApplication(activity);
			return;
		}
		OsmandApplication app = getMyApplication();
		// restore follow route mode
		if(app.getSettings().FOLLOW_THE_ROUTE.get() && !app.getRoutingHelper().isRouteCalculated()){
			final Intent mapIndent = new Intent(this, OsmandIntents.getMapActivity());
			startActivityForResult(mapIndent, 0);
			return;
		}
		startProgressDialog = new ProgressDialog(this);
		getMyApplication().checkApplicationIsBeingInitialized(this, startProgressDialog);
		SharedPreferences pref = getPreferences(MODE_WORLD_WRITEABLE);
		boolean firstTime = false;
		if(!pref.contains(FIRST_TIME_APP_RUN)){
			firstTime = true;
			pref.edit().putBoolean(FIRST_TIME_APP_RUN, true).commit();
			pref.edit().putString(VERSION_INSTALLED, Version.getFullVersion(app)).commit();
			
			applicationInstalledFirstTime();
		} else {
			int i = pref.getInt(TIPS_SHOW, 0);
			if (i < 7){
				pref.edit().putInt(TIPS_SHOW, ++i).commit();
			}
			boolean appVersionChanged = false;
			if(!Version.getFullVersion(app).equals(pref.getString(VERSION_INSTALLED, ""))){
				pref.edit().putString(VERSION_INSTALLED, Version.getFullVersion(app)).commit();
				appVersionChanged = true;
			}
						
			if (i == 1 || i == 5 || appVersionChanged) {
				TipsAndTricksActivity tipsActivity = new TipsAndTricksActivity(this);
				Dialog dlg = tipsActivity.getDialogToShowTips(!appVersionChanged, false);
				dlg.show();
			} else {
				if (startProgressDialog.isShowing()) {
					startProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							checkVectorIndexesDownloaded();
						}
					});
				} else {
					checkVectorIndexesDownloaded();
				}
			}
		}
		checkPreviousRunsForExceptions(firstTime);*/
	}


	
	
}
