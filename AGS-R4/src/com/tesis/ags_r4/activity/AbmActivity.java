package com.tesis.ags_r4.activity;

import com.tesis.ags_r4.AgsIntents;
import com.tesis.ags_r4.R;
import com.tesis.ags_r4.R.id;
import com.tesis.ags_r4.R.layout;
import com.tesis.ags_r4.R.string;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class AbmActivity extends Activity implements OnPreferenceClickListener{

	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.abm_lugar);
		about = new Preference(this);
		about.setOnPreferenceClickListener(this);
		about.setSummary(R.string.abm_button);
		about.setTitle(R.string.abm_button);
		about.setKey("about");
		//screen.addPreference(about);
		
		Window window = getWindow();
		final Activity activity = this;
		
		View cargarButton = window.findViewById(R.id.cargarButton);
		cargarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {//cuando vaya a cargar en oncreater obtener los puntos lat y lon
				Toast.makeText(getBaseContext(), R.string.cargar, Toast.LENGTH_LONG)
	            .show();
			}
		});
		
		cargarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	             // Perform action on click
				final Intent cargar = new Intent(activity, AgsIntents.getCargarActivity());
				activity.startActivity(cargar);
	            return true;
	         }
			
		});
		
		View eliminarButton = window.findViewById(R.id.eliminarButton);
		eliminarButton.setOnClickListener(new OnClickListener() {//deberia abrir otra para la busqueda(y listar categorias, y los lugares que contiene)
			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), R.string.eliminar, Toast.LENGTH_LONG)
	            .show();
			}
		});
		eliminarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	             // Perform action on click
				final Intent eliminar = new Intent(activity, AgsIntents.getSelecCatActivity());
				eliminar.putExtra("boton", "eliminar");
				activity.startActivity(eliminar);
	            return true;
	         }
			
		});
		
		
		View editarButton = window.findViewById(R.id.editarButton);
		editarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {//deberia abrir otra para la busqueda
				Toast.makeText(getBaseContext(), R.string.editar, Toast.LENGTH_LONG)
	            .show();
			}
		});
		
		editarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
	             // Perform action on click
				final Intent editar = new Intent(activity, AgsIntents.getSelecCatActivity());
				editar.putExtra("boton", "editar");
				activity.startActivity(editar);
	            return true;
	         }
			
		});
		
	}
	@Override
	public boolean onPreferenceClick(Preference preference) {
		return false;
	}
}
