package com.tesis.ags_r4;

import com.tesis.ags_r4.activity.AbmActivity;
import com.tesis.ags_r4.activity.CargarActivity;
import com.tesis.ags_r4.activity.EditarActivity;
import com.tesis.ags_r4.activity.EliminarActivity;
import com.tesis.ags_r4.activity.FavoritosActivity;
import com.tesis.ags_r4.activity.InfActivity;
import com.tesis.ags_r4.activity.SelectCatActivity;


public class AgsIntents {

	public static Class<AbmActivity> getAbmActivity(){
		return AbmActivity.class;
	}
	
	public static Class<FavoritosActivity> getFavoritosActivity(){
		return FavoritosActivity.class;
	}
	
	public static Class<InfActivity> getInfActivity(){
		return InfActivity.class;
	}
	
	public static Class<CargarActivity> getCargarActivity(){
		return CargarActivity.class;
	}
	
	public static Class<EliminarActivity> getEliminarActivity(){
		return EliminarActivity.class;
	}
	
	public static Class<EditarActivity> getEditarActivity(){
		return EditarActivity.class;
	}
	
	public static Class<SelectCatActivity> getSelecCatActivity(){
		return SelectCatActivity.class;
	}
	
}
