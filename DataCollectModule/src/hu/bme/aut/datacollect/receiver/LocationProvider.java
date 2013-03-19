package hu.bme.aut.datacollect.receiver;

import hu.bme.aut.datacollect.db.LocationDao;
import hu.bme.aut.datacollect.entity.LocationData;

import java.util.Calendar;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class LocationProvider implements LocationListener{
	
	private final LocationManager locationManager;
	
	private LocationDao locationDao = null;
	
	public LocationProvider(Context context, LocationDao locationDao){
			
		this.locationDao = locationDao;
		
		//request location updates
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public static GsmCellLocation getGsmCellLocation(Context context){
		
		GsmCellLocation location = null;
		
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
		    location = (GsmCellLocation) telephonyManager.getCellLocation();
		}
		return location;
	}

	@Override
	public void onLocationChanged(Location location) {
		// insert new location
		locationDao.create(new LocationData(Calendar.getInstance()
				.getTimeInMillis(), location.getLatitude(), location
				.getLongitude(), location.getAltitude()));
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public void registerListener(){
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 100, this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100, this);
	}
	
	public void unregisterListener(){
		locationManager.removeUpdates(this);
	}
	
}
