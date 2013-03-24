package hu.bme.aut.datacollect.receiver;

import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.db.LocationDao;
import hu.bme.aut.datacollect.entity.LocationData;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class LocationProvider implements LocationListener{
	
	private final LocationManager locationManager;	
	private LocationDao locationDao = null;
	private Context mContext;
	
	public LocationProvider(Context context, LocationDao locationDao){
			
		this.mContext = context;
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
	
	public void registerListener() {
		// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// 60000, 100, this);
		final boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsEnabled) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							enableLocationSettings();
						}
					})
					.setNegativeButton("Most nem", null)
					.setTitle("GPS enged�lyez�se")
					.setMessage(
							"A pontos helyeadatok gy�jt�s�hez sz�ks�ges bekapcsolni a GPS-t. Akarja most bekapcsolni?")
					.create().show();
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				6000, 1, this);
	}
	
	public void unregisterListener(){
		locationManager.removeUpdates(this);
	}
	
	private void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    mContext.startActivity(settingsIntent);
	}
	
}
