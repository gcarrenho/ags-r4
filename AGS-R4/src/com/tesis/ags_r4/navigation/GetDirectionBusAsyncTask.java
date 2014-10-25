package com.tesis.ags_r4.navigation;

import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Document;

import com.google.android.gms.maps.model.LatLng;
import com.tesis.ags_r4.GuiarMapa;
import com.tesis.ags_r4.R;
import com.tesis.ags_r4.activity.InfActivity;
import com.tesis.ags_r4.file.MakeFile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetDirectionBusAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList>{


	public static final String USER_CURRENT_LAT = "user_current_lat";
    public static final String USER_CURRENT_LONG = "user_current_long";
    public static final String DESTINATION_LAT = "destination_lat";
    public static final String DESTINATION_LONG = "destination_long";
    public static final String DIRECTIONS_MODE = "directions_mode";
	private InfActivity activity;
	private Exception exception;
	private ProgressDialog progressDialog;
	private MakeFile mfile=new MakeFile();

	public GetDirectionBusAsyncTask(InfActivity activity)
	{
		super();
		this.activity = activity;
	}

	public void onPreExecute()
	{
		progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage("Actualizando Informacion");
		progressDialog.show();
	}

	@Override
	public void onPostExecute(ArrayList result)
	{
		progressDialog.dismiss();
		if (exception == null)
		{
			activity.handleGetDirectionsResult(result);
		}
		else
		{
			processException();
		}
	}

	@Override
	protected ArrayList doInBackground(Map<String, String>... params)
	{
		Map<String, String> paramMap = params[0];
		try
		{
			ArrayList ar=new ArrayList();
			//LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
			//LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
			GBusDirection md = new GBusDirection();
			Document doc;
			//guardar en algun archivo o en la bd
			for(int i=1;i<19;i++){
				if (i==1 || i==2 || i==8 || i==9){
					doc=md.getDocument(String.valueOf(i)+"r");
					String directionPoints = md.getCoorValue(doc);
					ar.add(directionPoints);
					//Agregar
					mfile.grabar(String.valueOf(i)+"r", directionPoints);
					doc=md.getDocument(String.valueOf(i)+"v");
					directionPoints = md.getCoorValue(doc);
					ar.add(directionPoints);
					mfile.grabar(String.valueOf(i)+"v", directionPoints);
					//Agregar
				}else{
					doc=md.getDocument(String.valueOf(i));
					String directionPoints = md.getCoorValue(doc);
					ar.add(directionPoints);
					//Agregar
					mfile.grabar(String.valueOf(i), directionPoints);
				}
				
				//a medida que vamos descargando, vamos a ir guardandolo en un archivo
				//usando como nombre el numero de linea.
			}
			return ar;
		}
		catch (Exception e)
		{
			exception = e;
			return null;
		}
	}

	private void processException()
	{
		Toast.makeText(activity, activity.getString(R.string.error_when_retrieving_data), 3000).show();
	}
	
}
