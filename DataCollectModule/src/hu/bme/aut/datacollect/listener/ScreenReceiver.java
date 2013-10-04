package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.ScreenData;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver implements IListener {

	private static final String TAG = "DataCollect:ScreenReceiver";
	
	private DaoBase<ScreenData> dao = null;
	private Context mContext = null;
	
	private boolean regScreen = false;
	
	public ScreenReceiver(Context context, DaoBase<ScreenData> packageDao) {
		super();
		this.dao = packageDao;
		this.mContext = context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
			
			Log.d(TAG, String.format("ScreenData: on"));
			dao.create(new ScreenData(Calendar.getInstance().getTimeInMillis(), "on"));
		}
		else {
			
			Log.d(TAG, String.format("ScreenData: off"));
			dao.create(new ScreenData(Calendar.getInstance().getTimeInMillis(), "off"));
		}	
	}
	
	@Override
	public void register() {

		if (!this.regScreen){
			IntentFilter filter = new IntentFilter();
	        filter.addAction(Intent.ACTION_SCREEN_ON);
	        filter.addAction(Intent.ACTION_SCREEN_OFF);
	        
	        this.mContext.registerReceiver(this, filter);
	        
	        this.regScreen = true;
		}

	}

	@Override
	public void unregister() {
		
		if (this.regScreen){
			this.mContext.unregisterReceiver(this);
			this.regScreen = false;
		}
	}
	
	@Override
	public boolean isAvailable() {
		//receiving screen events is available on every device
		return true;
	}
}
