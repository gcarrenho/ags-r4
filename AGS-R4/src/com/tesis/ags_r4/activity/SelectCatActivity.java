package com.tesis.ags_r4.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tesis.ags_r4.AgsIntents;
import com.tesis.ags_r4.ExpandableListAdapter;
import com.tesis.ags_r4.Lugar;
import com.tesis.ags_r4.R;
import com.tesis.ags_r4.R.id;
import com.tesis.ags_r4.R.layout;
import com.tesis.ags_r4.location.MyLocationListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import android.view.View;

public class SelectCatActivity extends Activity{

	private Map<String, List<String>> collLugares;
	List<String> listCat;
    List<String> listLugares;
	private ExpandableListView expListView;
	private Lugar lugarBd;
	private List<Lugar> l;
	private static final int ELIMINAR_REQUEST_CODE = 0;
	private static final int EDITAR_REQUEST_CODE = 1;

	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";

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
		Window window = getWindow();
		final Activity activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		lugarBd= new Lugar(this);
		lugarBd.open();
		l=lugarBd.getAllLugares();
		
		setContentView(R.layout.list_cat);
		createListCat();
		createCollecctionLugares();
		//
		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.expandableListViewCat);
        
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                this, listCat, collLugares);
        expListView.setAdapter(expListAdapter);
        
		String boton=intent.getStringExtra("boton");
		if(boton.equalsIgnoreCase("eliminar")){//apreto boton eliminar
			//eliminar el lugar de la base de datos..
			 expListView.setOnChildClickListener(new OnChildClickListener() {
				 
		            public boolean onChildClick(ExpandableListView parent, View v,
		                int groupPosition, int childPosition, long id) {
		                String nombre=(String) expListAdapter.getChild(groupPosition, childPosition);
			              
			              lugarBd.deleteLugar(nombre);
			              Toast.makeText(getBaseContext(),"Lugar "+nombre+" Eliminado Correctamente", Toast.LENGTH_LONG)
	                        .show();
			              actualizarLista();
			              lugarBd.close();
		                return true;
		            }
		        });
			 
			 //Presiono el Padre
			 /*expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
			        			                return true;
			        }
			    });*/
	
			 //Click Prolongado en el item
			 /*expListView.setOnItemLongClickListener(new ExpandableListView.OnItemLongClickListener() {
			     public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
			    	  
			    	 int itemType = ExpandableListView.getPackedPositionType(id);

			    	 //El item presionado pertenece a un hijo
			          if ( itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			              
			              return true; //true if we consumed the click, false if not

			          } else if(itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {//sino pertenece al padre
			              int groupPosition = ExpandableListView.getPackedPositionGroup(id);
			              expListView.expandGroup(groupPosition);
			              //do your per-group callback here
			              return true; //true if we consumed the click, false if not

			          } else {
			              // null item; we don't consume the click
			              return false;
			          }			    	
		    }
			     
		}); */
			 
		}else if (boton.equalsIgnoreCase("editar")){//apreto boton editar
			//abrir el evento para editar el lugar
			 expListView.setOnChildClickListener(new OnChildClickListener() {
				 
		            public boolean onChildClick(ExpandableListView parent, View v,
		                    int groupPosition, int childPosition, long id) {
		                final String selected = (String) expListAdapter.getChild(
		                        groupPosition, childPosition);
		                String nombre=(String) expListAdapter.getChild(groupPosition, childPosition);
			              Lugar l=lugarBd.getLugar(nombre);
			              //voy a tener q diferenciar que vas desde aca, sino cuando de a aceptar me va
			              //a decir que con ese nombre ya existe.
			              final Intent cl = new Intent(activity, AgsIntents.getCargarActivity());
			              cl.putExtra("accion", "editar");
			              cl.putExtra("nombre", l.getNombre());
			              cl.putExtra("tipo", l.getCategoria());
			              cl.putExtra("tel", l.getTel());
			              cl.putExtra("lat", l.getLatitud());
			              cl.putExtra("long", l.getLongitud());
			              //Cargar con estos datos, y buscar luego en la base de dato y solo
			              //modificar el valor deseado.
			              activity.startActivityForResult(cl, EDITAR_REQUEST_CODE);
		 
		                return true;
		            }
		        });
			 			 		
		}else{//el boton es para guiar
			expListView.setOnChildClickListener(new OnChildClickListener() {
				 
	            public boolean onChildClick(ExpandableListView parent, View v,
	                    int groupPosition, int childPosition, long id) {
	         
			              String nombre=(String) expListAdapter.getChild(groupPosition, childPosition);
			              //Buscarlo en la base de datos y traer todos los datos.
			              //lugarBd.deleteLugar(nombre);
			              final Intent guiar = new Intent(activity, AgsIntents.getGuiarMapa());
			              String nom=(String) expListAdapter.getChild(groupPosition, childPosition);
			              Lugar l=lugarBd.getLugar(nom);
			              guiar.putExtra("lat", l.getLatitud());
			              guiar.putExtra("lng", l.getLongitud());
			              activity.startActivity(guiar);
			              lugarBd.close();
	 
	                return true;
	            }
	        });
		
		}
	}

	//Metodo que crea la lista de las categorias existentes.
	  private void createListCat(){
		  listCat= new ArrayList<String>();
		  int i=0;
		  while (i<l.size()){
			  if(!listCat.contains(l.get(i).getCategoria())){
				  listCat.add(l.get(i).getCategoria());
			  }
			i++;
		  }
	  }
	  
	  //Metodo que hace un mapeo entre las categorias y los lugares que estan el esa misma categoria.
	  private void createCollecctionLugares(){
		  List<String> nomLugar;
		  collLugares = new LinkedHashMap<String, List<String>>();
		  int i=0,j;
		  while (i<listCat.size()){
			  j=0;
			  nomLugar=new ArrayList<String>();
				  while (j<l.size()){
					  if (listCat.get(i).equals(l.get(j).getCategoria())){
						nomLugar.add(l.get(j).getNombre());
						
					  }
					  j++;
				  }
				  collLugares.put(listCat.get(i),nomLugar);
			i++;
		  }
	  }
	  
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
	      if (resultCode == RESULT_CANCELED) {
	          // Si es así mostramos mensaje de cancelado por pantalla.
	          Toast.makeText(this, "Accion Cancelada", Toast.LENGTH_SHORT)
	                  .show();
	      } else {
	          // De lo contrario, recogemos el resultado de la segunda actividad.
	          //String resultado = data.getExtras().getString("RESULTADO");
	          // Y tratamos el resultado en función de si se lanzó para rellenar el
	          // nombre o el apellido.
	    	   	lugarBd.open();
	   			this.actualizarLista();
	           lugarBd.close();
	      }
	  }
	  
	  public void actualizarLista(){
		  l=lugarBd.getAllLugares();
 			createListCat();
 			createCollecctionLugares();
  	   final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(this, listCat, collLugares);
         expListView.setAdapter(expListAdapter);
	  }
}
