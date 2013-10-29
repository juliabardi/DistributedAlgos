package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.ConnectivityData;
import hu.bme.aut.datacollect.utils.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

public class ConnectivityReceiver extends AbstractReceiver {
	
	private static final String TAG = "DataCollect:ConnectivityData";
	
	private DaoBase<ConnectivityData> connDao = null;
	
	private boolean regConn = false;
	
	private ConnectivityManager connManager;

	public ConnectivityReceiver(DataCollectService context, DaoBase<ConnectivityData> connDao) {
		super(context);
		this.connDao = connDao;
		
		connManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		registerSelf();
	
		this.createConnectivityData();
	}
	
	protected void createConnectivityData(){
		
		Map<String,String> addresses = this.getLocalIpAddresses();
		String wifiAddress = null;
		String gsmAddress = null;
		for (String key : addresses.keySet()){
			if (key.contains("rmnet")){
				gsmAddress = addresses.get(key);
			}
			if (key.contains("wlan")){
				wifiAddress = addresses.get(key);
			}
		}
				
		NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
		boolean isConnected = false;
		int type = -1;
		if (activeNetwork != null){
			isConnected = activeNetwork.isConnectedOrConnecting();
			if (isConnected){
				type = activeNetwork.getType();
			}
		}
		
		Log.d(TAG, String.format("ConnectivityData: connected: %s, type: %s, wifiAddress: %s, gmsAddress: %s", isConnected, type, wifiAddress, gsmAddress));
		this.updatePreference(wifiAddress);
		
		if (DataCollectService.isDataTypeEnabled(mContext, getDataType())){
			
			ConnectivityData last = this.connDao.queryLast();
			if (last != null && isConnected == last.isConnected() && type == last.getType() &&
					StringUtils.equals(wifiAddress, last.getWifiAddress()) && 
					StringUtils.equals(gsmAddress, last.getGsmAddress())){
				Log.d(TAG, "No change, returning. Last: " + last.toString());
				return;
			}
			//saving to the DB only if collecting is enabled
			connDao.create(new ConnectivityData(Calendar.getInstance().getTimeInMillis(), isConnected, type,
					wifiAddress, gsmAddress));
		}
		
		this.mContext.sendRecurringRequests();
	}
	
	private void updatePreference(String ip){
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
		
		if (!StringUtils.equals(settings.getString(DataCollectService.DEVICE_IP_WIFI, null), ip)){
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(DataCollectService.DEVICE_IP_WIFI, ip);
			editor.commit();
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
				
		this.createConnectivityData();
	}
	
	private void registerSelf(){
		
		if (!regConn){
			IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
			mContext.registerReceiver(this, intentFilter);
			regConn = true;
		}
	}
	
	public void unregisterSelf(){
		
		if (regConn){
			mContext.unregisterReceiver(this);
			regConn = false;
		}
		mContext.deleteRecurringRequests(getDataType());
	}

	@Override
	public void register() {
		//leaving it blank, because ConnectivityReceiver should run in the background all the time
	}

	@Override
	public void unregister() {
		
		mContext.deleteRecurringRequests(getDataType());
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	public Map<String,String> getLocalIpAddresses() {
		
		Map<String,String> addresses = new HashMap<String,String>();
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
	                    addresses.put(intf.getName(), inetAddress.getHostAddress());
	                    //Log.d(TAG, String.format("Device local IP address: %s: %s", intf.getName(), inetAddress.getHostAddress()));
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        ex.printStackTrace();
	    }
	    return addresses;
	}

	@Override
	public String getDataType() {
		return DataCollectService.CONNECTIVITY;
	}
}
