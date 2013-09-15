package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.db.DaoBase;
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

public class LocationProvider implements LocationListener, IListener{
	
	private final LocationManager locationManager;	
	private DaoBase<LocationData> locationDao = null;
	private Context mContext;
	
	public LocationProvider(Context context, DaoBase<LocationData> locationDao){
			
		this.mContext = context;
		this.locationDao = locationDao;
		
		//request location updates
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void setContext(Context context){
		this.mContext = context;
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
	
	@Override
	public void register() {
		// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// 60000, 100, this);
		final boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsEnabled) {
			//alert dialog cannot be opened with a service as context
			//TODO inject activity as context somehow
//			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//			builder.setPositiveButton("OK",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int id) {
//							enableLocationSettings();
//						}
//					})
//					.setNegativeButton("Most nem", null)
//					.setTitle("GPS engedélyezése")
//					.setMessage(
//							"A pontos helyeadatok gyûjtéséhez szükséges bekapcsolni a GPS-t. Akarja most bekapcsolni?")
//					.create().show();
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				6000, 1, this);
	}
	
	@Override
	public void unregister(){
		locationManager.removeUpdates(this);
	}
	
	void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    mContext.startActivity(settingsIntent);
	}
	
	@Override
	public boolean isAvailable() {
		//in absence of gps provider should i use network provider?
		if (this.locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
			return true;
		}
		return false;
	}
}
