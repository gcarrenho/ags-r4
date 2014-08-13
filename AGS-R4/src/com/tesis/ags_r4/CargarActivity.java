package com.tesis.ags_r4;

import java.util.List;

import com.tesis.ags_r4.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView.BufferType;
//CERRAR LA BASE DE DATOS CUANDO PRESIONA EL BOTON ATRAS
public class CargarActivity extends Activity{
	
	//private Lugar lugar, lugar1;
	//private LugaresDb lugarDb= new  LugaresDbHelper(this,"lugares",null,1);
	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";
	private static final BufferType EDITABLE = null;
	private Lugar lugarBd;
	private Preference about;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean exit = false;
		if(getIntent() != null){
			Intent intent = getIntent();
			if(intent.getExtras() != null && intent.getExtras().containsKey(APP_EXIT_KEY)){
				exit = true;
			}
		}
		lugarBd= new Lugar(this);
		lugarBd.open();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cargar_lugar);
		Window window = getWindow();
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		final List<Lugar> listaLugares=lugarBd.getAllLugares();
		
		//Eventos sobre los campos de texto.
		//----------------------------------
		//Evento sobre campo de texto nombre del lugar
		View lugarTexNom = window.findViewById(R.id.textNomLugar);
		lugarTexNom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				Toast.makeText(getBaseContext(), R.string.nombre_lugar, Toast.LENGTH_LONG)
	            .show();
			}
		});
		lugarTexNom.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	            //Abrir el reconocedor de vos
	            return true;
	         }
			
		});
		//Evento sobre el campo de texto Tipo de Lugar
		View tipoLugarText = window.findViewById(R.id.textTipoLugar);
		tipoLugarText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				Toast.makeText(getBaseContext(), R.string.cat_lugar, Toast.LENGTH_LONG)
	            .show();
			}
		});
		tipoLugarText.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	            //Abrir el reconocedor de vos
	            return true;
	         }
			
		});
		
		//Evento sobre el campo de texto Telefono del lugar
		View telLugarText = window.findViewById(R.id.textTelLugar);
		telLugarText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				Toast.makeText(getBaseContext(), R.string.tel_lugar, Toast.LENGTH_LONG)
	            .show();
			}
		});
		telLugarText.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	            //Abrir el reconocedor de vos
	            return true;
	         }
			
		});
		
		
		//Aceptar dar de alta en la base de datos
		//cancelar, salir volver ?atras?
		View aceptarButton = window.findViewById(R.id.aceptarButtom);
		aceptarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				Toast.makeText(getBaseContext(), R.string.aceptar, Toast.LENGTH_LONG)
	            .show();
			}
		});
		aceptarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				//Falta obtener las coordenadas y setearlas..
				EditText nombre=(EditText)findViewById(R.id.textNomLugar);
				lugarBd.setNombre(nombre.getText().toString());
				EditText cat=(EditText)findViewById(R.id.textTipoLugar);
				lugarBd.setCategoria(cat.getText().toString());
				EditText tel=(EditText)findViewById(R.id.textTelLugar);
				String telefono = tel.getText().toString();
			    final int telInt = Integer.parseInt(telefono);
				lugarBd.setTel(telInt);
				
				if(lugarBd.ExisteLugar(nombre.getText().toString())){//true si tiene algo
					//lanzar un cartel diciendo que ya existe cargado un lugar con dicho nombre poner otro
					//identificador , y poner  el foco en el campo nombre
				}else{
					try
				    {
						lugarBd.createLugar(lugarBd);
						boolean exl=lugarBd.ExisteLugar("No lo cases");
						 EditText cat1=(EditText)findViewById(R.id.textNomLugar);
				         cat1.setText(String.valueOf(exl), EDITABLE);
						
						 //fillData();//este metodo esta para probar solamente..
						 //Cartel diciendo que se cargo y volver atras.
				         lugarBd.close();
				         finish();
					 }
					 catch(Throwable t)
					 {
					            Log.e("ERROOOOOOOR", "Mensaje de error", t);
					 }
					
				}
	            return true;
	         }
			
		});
		
		View cancelarButton = window.findViewById(R.id.cancelarButtom);
		cancelarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), R.string.cancelar, Toast.LENGTH_LONG)
	            .show();
			}
		});
		cancelarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	             // Perform action on click
				lugarBd.close();
				finish();
	            return true;
	         }
			
		});
		
	}	
	
	private void fillData(){
		          Cursor remindersCursor=lugarBd.ferchAllLugares();
		          startManagingCursor(remindersCursor);
		          
		          int columnas=remindersCursor.getColumnIndex(Lugar.KEY_LAT);
		          EditText cat=(EditText)findViewById(R.id.textNomLugar);
		          cat.setText(remindersCursor.getColumnName(columnas), EDITABLE);
		    } 
	
}
