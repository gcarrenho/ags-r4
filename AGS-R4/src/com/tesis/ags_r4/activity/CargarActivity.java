package com.tesis.ags_r4.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tesis.ags_r4.Lugar;
import com.tesis.ags_r4.R;
import com.tesis.ags_r4.R.id;
import com.tesis.ags_r4.R.layout;
import com.tesis.ags_r4.R.string;
import com.tesis.ags_r4.location.MyLocationListener;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.Preference;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView.BufferType;
import android.location.Address;


@SuppressLint("NewApi")
public class CargarActivity extends Activity implements OnInitListener{

	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";
	private static final BufferType EDITABLE = null;
	private Lugar lugarBd;
	private TextToSpeech tts;
	private  int campo;
	private double latitud=0.0, longitud=0.0;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
	private Preference about;
	private LocationManager locManager;
	private MyLocationListener locListener;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean exit = false;
		Intent intent = getIntent();
		if(intent != null){
			if(intent.getExtras() != null && intent.getExtras().containsKey(APP_EXIT_KEY)){
				exit = true;
			}
		}
		lugarBd= new Lugar(this);
		lugarBd.open();
		tts = new TextToSpeech(this, this);
		//this.configGps();
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locListener = new MyLocationListener(){
			@Override
			public void onLocationChanged(Location loc) {
				if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
					latitud=loc.getLatitude();
					longitud=loc.getLongitude();
				}
			}
		};
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cargar_lugar);
		Window window = getWindow();
		String accion=intent.getStringExtra("accion");
		if (accion.equals("editar")){
			String nombre=intent.getStringExtra("nombre");
			String tipo=intent.getStringExtra("tipo");
			int telefono=intent.getIntExtra("tel", 0);
			double lat=intent.getDoubleExtra("lat", 0.0);
			double lon=intent.getDoubleExtra("long", 0.0);
			EditText nom=(EditText)findViewById(R.id.textNomLugar);
			nom.setText(nombre);
			EditText cat=(EditText)findViewById(R.id.textTipoLugar);
			cat.setText(tipo);
			EditText tel=(EditText)findViewById(R.id.textTelLugar);
			tel.setText(String.valueOf(telefono));
			//con estos datos llenar los campos
			this.editar(nombre,lat, lon);
		}else{
			this.cargar();
			locManager.removeUpdates(locListener);
		}
		View cancelarButton = window.findViewById(R.id.cancelarButtom);
		//Evento que escucha el click sobre el boton cancelar
		cancelarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lugarBd.close();
				setResult(RESULT_CANCELED);
				locManager.removeUpdates(locListener);
				finish();
			}
		});

		//Evento que escucha un click prolongado sobre el boton cancelar
		cancelarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				return true;
			}

		});
	}


	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 * Llamada cuando la actividad va a empezar a
	 * interactuar con el usuario, en este punto es 
	 * el último punto antes de que el usuario ya vea la actividad
	 * y pueda empezar a interactuar con ella. Siempre despues de un 
	 * onResume() viene un onPause().
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.configGps();
	}

	@Override
	protected void onStop() {
		super.onStop();
		locManager.removeUpdates(locListener);
	}

	

	/*
	 *  LLamada cuando el sistema va a empezar una nueva actividad.
	 *  Ésta necesita parar animaciones, y parar todo lo que esté haciendo.
	 *  Hay que intentar que esta llamada dure poco tiempo, porque hasta que no 
	 *  se ejecute este método no arranca la siguiente actividad. Después de esta 
	 *  llamada puede venir un onResume() si la actividad vuelve a primer plano o
	 *  un onStop() si se hace invisible para el usuario.
	 *   
	 */
	/*
	   @Override    protected void onPause() {
           super.onPause();
           locManager.removeUpdates(locListener);
     }*/



	@Override
	public void onInit(int code) {
		if (code==TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.getDefault());
		} else {
			tts = null;
			Toast.makeText(this, "Failed to initialize TTS engine.",Toast.LENGTH_SHORT).show();

		}

	}

	private void startVoiceRecognitionActivity(){
		// Definición del intent para realizar en análisis del mensaje
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// Indicamos el modelo de lenguaje para el intent
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// Definimos el mensaje que aparecerá 
		// intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Diga, Llamar a ...");
		// Lanzamos la actividad esperando resultados

		//HAY UN PROBLEMA LA MINA DEL TALKBACK DICE "BUSQUDA DE GOOGLE", CUANDO ESTA RECONOCIENDO.
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	//Recogemos los resultados del reconocimiento de voz
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){	
		if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK){
			//El intent nos envia un ArrayList aunque en este caso solo 
			//utilizaremos la pos.0
			if (campo==1){
				ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				EditText nombre=(EditText)findViewById(R.id.textNomLugar);
				nombre.setText(matches.get(0));


			}else if (campo==2){
				ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				EditText tipo=(EditText)findViewById(R.id.textTipoLugar);
				tipo.setText(matches.get(0));	   
			}else if (campo==3){
				ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				EditText tel=(EditText)findViewById(R.id.textTelLugar);
				tel.setText(matches.get(0));	   
			}	
		}
	}


	//Metodo que se encarga del alta de un nuevo lugar
	public void cargar(){
		Window window = getWindow();
		this.eventosSobreCampos();
		//Aceptar dar de alta en la base de datos
		//cancelar, salir volver ?atras?
		View aceptarButton = window.findViewById(R.id.aceptarButtom);

		//Evento que escucha el click sobre el boton aceptar
		aceptarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				//Falta obtener las coordenadas y setearlas..
				EditText nombre=(EditText)findViewById(R.id.textNomLugar);
				lugarBd.setNombre(nombre.getText().toString().toLowerCase());
				EditText cat=(EditText)findViewById(R.id.textTipoLugar);
				lugarBd.setCategoria(cat.getText().toString().toLowerCase());
				EditText tel=(EditText)findViewById(R.id.textTelLugar);
				String telefono = tel.getText().toString().replace(" ", "");//El tel va a ser dictado eliminar espacios en blanco.
				if (telefono.isEmpty()){
					telefono="0";
				}
				
				final int telInt = Integer.parseInt(telefono);
				lugarBd.setTel(telInt);
				if(latitud==0.0 && longitud==0.0){
					Toast.makeText(getBaseContext(),R.string.obt_loc, Toast.LENGTH_LONG)
					.show();
				}else if(cat.getText().toString().length()==0){
					Toast.makeText(getBaseContext(),R.string.adv_tdl, Toast.LENGTH_LONG)
					.show();
				}else if(nombre.getText().toString().length()==0){
					Toast.makeText(getBaseContext(), R.string.adv_cn, Toast.LENGTH_LONG)
					.show();
				}else if(lugarBd.ExisteLugar(nombre.getText().toString().toLowerCase())){//true si tiene algo
					//lanzar un cartel diciendo que ya existe cargado un lugar con dicho nombre poner otro
					//identificador , y poner  el foco en el campo nombre	
					Toast.makeText(getBaseContext(), R.string.exi_l, Toast.LENGTH_LONG)
					.show();

				}else{
					try
					{
						Toast.makeText(getBaseContext(), R.string.loc_sat, Toast.LENGTH_LONG)
						.show();
						lugarBd.setLatitud(latitud);
						lugarBd.setLongitud(longitud);
						lugarBd.createLugar(lugarBd);
						Toast.makeText(getBaseContext(), R.string.l_carg, Toast.LENGTH_LONG)
						.show();
						lugarBd.close();
						finish();
					}
					catch(Throwable t)
					{
						Log.e("ERROOOOOOOR", "Mensaje de error", t);
					}

				}
			}
		});

		//Evento que escucha un click prolongado sobre el boto aceptar
		aceptarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				return true;
			}

		});
	}


	//Metedo que busca el lugar, si existe remplaza los campos por los actuales
	//y actualiza con los nuevos datos.
	public void editar(final String nombre,final double lat, final double lon){
		Window window = getWindow();
		this.eventosSobreCampos();
		//Aceptar dar de alta en la base de datos
		View aceptarButton = window.findViewById(R.id.aceptarButtom);

		//Evento que escucha el click sobre el boton aceptar
		aceptarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				EditText nom=(EditText)findViewById(R.id.textNomLugar);
				lugarBd.setNombre(nom.getText().toString().toLowerCase());
				EditText cat=(EditText)findViewById(R.id.textTipoLugar);
				lugarBd.setCategoria(cat.getText().toString().toLowerCase());
				EditText tel=(EditText)findViewById(R.id.textTelLugar);
				lugarBd.setLatitud(lat);
				lugarBd.setLongitud(lon);
				String telefono = tel.getText().toString().replace(" ", "");;//El tel va a ser dictado eliminar espacios en blanco.
				if (telefono.isEmpty()){
					telefono="0";
				}
				final int telInt = Integer.parseInt(telefono);
				lugarBd.setTel(telInt);

				if(lugarBd.ExisteLugar(nombre.toLowerCase())){
					//si existe actualizar la bd,
					//Actualizar Lista de lugares.
					lugarBd.actualizaLugar(nombre, lugarBd);
					setResult(RESULT_OK);
					finish();

				}else{//si no existe lanzar un cartel

				}

			}
		});

		//Evento que escucha un click prolongado sobre el boto aceptar
		aceptarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				return true;
			}

		});
	}

	public void eventosSobreCampos(){
		Window window = getWindow();
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		final List<Lugar> listaLugares=lugarBd.getAllLugares();
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		//Eventos sobre los campos de texto.
		//----------------------------------
		//Evento sobre campo de texto nombre del lugar
		final View lugarTexNom = window.findViewById(R.id.textNomLugar);
		//Se sobreescribe el evento de cuando se cambia el foco para que
		//no figure el teclado virtual en pantalla
		lugarTexNom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				lugarTexNom.post(new Runnable() {
					@Override
					public void run() {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(lugarTexNom.getWindowToken(), 0);
					}
				});
			}
		});

		//Evento que escucha el click sobre el campo
		lugarTexNom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				campo=1;
				startVoiceRecognitionActivity();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(lugarTexNom.getWindowToken(), 0);
			}
		});


		//Evento que escucha un click prolongado sobre el campo de texto
		lugarTexNom.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				//Abrir el reconocedor de vos
				return true;
			}

		});

		//Evento sobre el campo de texto Tipo de Lugar
		final View tipoLugarText = window.findViewById(R.id.textTipoLugar);

		//Se sobreescribe el evento de cuando se cambia el foco para que
		//no figure el teclado virtual en pantalla
		tipoLugarText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				tipoLugarText.post(new Runnable() {
					@Override
					public void run() {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(tipoLugarText.getWindowToken(), 0);
					}
				});
			}
		});

		//Evento que escucha el click sobre el campo
		tipoLugarText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				//Abrir el reconocedor de vos
				campo=2;
				startVoiceRecognitionActivity();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tipoLugarText.getWindowToken(), 0);
			}
		});

		//Evento que escucha un click prolongado sobre el campo de texto
		tipoLugarText.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				return true;
			}

		});

		//Evento sobre el campo de texto Telefono del lugar
		final View telLugarText = window.findViewById(R.id.textTelLugar);

		telLugarText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				telLugarText.post(new Runnable() {
					@Override
					public void run() {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(telLugarText.getWindowToken(), 0);
					}
				});
			}
		});

		//Evento que escucha el click sobre el campo
		telLugarText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				campo=3;
				//Abrir el reconocedor de vos
				startVoiceRecognitionActivity();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(telLugarText.getWindowToken(), 0);
			}
		});

		//Evento que escucha un click prolongado sobre el campo de texto
		telLugarText.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				return true;
			}

		});
	}



	public void configGps(){

		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,(LocationListener) locListener);

		/*LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		MyLocationListener locListener = new MyLocationListener();
		locListener.setMainActivity(this);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,(LocationListener) locListener);

		Criteria criterio = new Criteria();
	    criterio.setCostAllowed(false);
	    criterio.setAltitudeRequired(false);
	    criterio.setAccuracy(Criteria.ACCURACY_FINE);


	    String proveedor = locManager.getBestProvider(criterio, true);*/
		// log.("Mejor proveedor: " + proveedor + "\n");
		//log("Comenzamos con la última localización conocida:");
		// Location localizacion = locManager.getLastKnownLocation(proveedor);
		//LocationProvider info = locManager.getProvider(proveedor);
	}		
}
