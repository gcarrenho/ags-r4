package com.tesis.ags_r4.maps;

import com.google.android.maps.MapActivity;
import com.tesis.ags_r4.R;
import com.tesis.ags_r4.R.layout;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

//Se modifico en preopetir para que tome el MapActivity, pero no anda en el cel..
public class GuiarMapa extends MapActivity implements LocationListener{

	 	@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.guiar);
	    }

		@Override
		protected boolean isRouteDisplayed() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
}
