package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.ConnectivityData;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectivityReceiver extends BroadcastReceiver implements
		IListener {
	
	private static final String TAG = "DataCollect:ConnectivityData";
	
	private DaoBase<ConnectivityData> connDao = null;
	private Context mContext = null;
	
	private boolean regConn = false;
	
	private ConnectivityManager connManager;

	public ConnectivityReceiver(Context context, DaoBase<ConnectivityData> connDao) {
		super();
		this.connDao = connDao;
		this.mContext = context;
		
		connManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	protected void createConnectivityData(){
		
		NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
		boolean isConnected = false;
		int type = -1;
		if (activeNetwork != null){
			isConnected = activeNetwork.isConnectedOrConnecting();
			if (isConnected){
				type = activeNetwork.getType();
			}
		}
		Log.d(TAG, String.format("ConnectivityData: connected: %s, type: %s", isConnected, type));
		connDao.create(new ConnectivityData(Calendar.getInstance().getTimeInMillis(), isConnected, type));
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
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

}
