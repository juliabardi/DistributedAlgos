package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.ActivityFragmentSettings;
import hu.bme.aut.datacollect.activity.DataCollectService;
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
import android.util.Log;

public class LocationProvider implements LocationListener, IListener{
	
	private static final String TAG = "DataCollect:LocationProvider";
	
	protected DataCollectService mContext;
	
	private final LocationManager locationManager;	
	private DaoBase<LocationData> locationDao = null;
	
	public LocationProvider(DataCollectService context, DaoBase<LocationData> locationDao){
		this.mContext = context;		
		this.locationDao = locationDao;
		
		//request location updates
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	//where should i use this?
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
		this.createLocationData(location);
	}
	
	private void createLocationData(Location location){
		
		Log.d(TAG, String.format("LocationData: lat: %s, lon: %s, alt: %s", location.getLatitude(), 
				location.getLongitude(), location.getAltitude()));
		locationDao.create(new LocationData(Calendar.getInstance()
				.getTimeInMillis(), location.getLatitude(), location
				.getLongitude(), location.getAltitude()));
		
		this.mContext.sendRecurringRequests();
	}

	@Override
	public void onProviderDisabled(String provider) {
		
		Log.d(TAG, String.format("Provider disabled: %s",provider));	
		if (LocationManager.GPS_PROVIDER.equals(provider)){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		
		Log.d(TAG, String.format("Provider enabled: %s",provider));	
		if (LocationManager.GPS_PROVIDER.equals(provider)){
			locationManager.removeUpdates(this);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
		}
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void register() {
		
		if (!isGPSEnabled()) {
			if (ActivityFragmentSettings.instance != null)
				this.showAlert(ActivityFragmentSettings.instance);
		}
		
		if (isGPSEnabled()){
			Log.d(TAG, "Registering to GPS Provider");
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
			//create a LocationData with the last known location
			this.createLocationData(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
			return;
		}
		//register both to get notification when one of them is enabled
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
		
		if (isNetworkProviderEnabled()) {
			//create a LocationData with the last known location
			this.createLocationData(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		}
	}
	
	public boolean isGPSEnabled(){
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public boolean isNetworkProviderEnabled(){
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	public void showAlert(final Context context){
		
		String message = "Helyadatok gyûjtéséhez szükséges bekapcsolni a GPS-t vagy a hálózati szolgáltatást. Akarja most bekapcsolni valamelyiket?";
		if (isNetworkProviderEnabled()){
			message = "Pontos helyadatok gyûjtéséhez szükséges bekapcsolni a GPS-t. Akarja most bekapcsolni?";
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						enableLocationSettings(context);
					}
				})
				.setNegativeButton("Most nem", null)
				.setTitle("Helyadatok engedélyezése")
				.setMessage(message)
				.create().show();
	}
	
	@Override
	public void unregister(){
		locationManager.removeUpdates(this);
		mContext.deleteRecurringRequests(getDataType());
	}
	
	void enableLocationSettings(Context context) {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    context.startActivity(settingsIntent);
	}
	
	@Override
	public boolean isAvailable() {
		//in absence of gps provider should i use network provider?
		if (this.locationManager.getProvider(LocationManager.GPS_PROVIDER) != null
				|| this.locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
			return true;
		}
		return false;
	}

	@Override
	public String getDataType() {
		return DataCollectService.LOCATION;
	}
}
