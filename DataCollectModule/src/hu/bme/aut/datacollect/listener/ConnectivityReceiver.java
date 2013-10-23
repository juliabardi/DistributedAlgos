package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.ConnectivityData;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
		ConnectivityData last = this.connDao.queryLast();
		if (last != null && isConnected == last.isConnected() && type == last.getType()){
			//same as the last, dont save
			return;
		}
		
		Log.d(TAG, String.format("ConnectivityData: connected: %s, type: %s, wifiAddress: %s, gmsAddress: %s", isConnected, type, wifiAddress, gsmAddress));
		connDao.create(new ConnectivityData(Calendar.getInstance().getTimeInMillis(), isConnected, type,
				wifiAddress, gsmAddress));
		
		this.mContext.sendRecurringRequests();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.createConnectivityData();
	}

	@Override
	public void register() {
		
		if (!regConn){
			IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
			mContext.registerReceiver(this, intentFilter);
			regConn = true;
		}
	}

	@Override
	public void unregister() {
		
		if (regConn){
			mContext.unregisterReceiver(this);
			regConn = false;
		}
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
