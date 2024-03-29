package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.BatteryData;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryReceiver extends AbstractReceiver {
	
	private static final String TAG = "DataCollect:BatteryReceiver";
	
	private DaoBase<BatteryData> batteryDao = null;
	
	private boolean regBattery = false;

	public BatteryReceiver(DataCollectService context, DaoBase<BatteryData> batteryDao) {
		super(context);
		this.batteryDao = batteryDao;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = (level / (float)scale) * 100;
		
		BatteryData last = this.batteryDao.queryLast();
		if (last!=null && batteryPct == last.getPercent() && status == last.getStatus() 
				&& chargePlug == last.getPlugged()){
			//no need to save, nothing changed
			return;
		}
		
		Log.d(TAG, String.format("BatteryData: %s %%, status: %s , plugged: %s", 
				batteryPct, status, chargePlug));
		this.batteryDao.create(new BatteryData(Calendar.getInstance().getTimeInMillis(), 
				batteryPct, status, chargePlug));
		
		this.mContext.sendRecurringRequests();	
	}

	@Override
	public void register() {
		
		if (!this.regBattery){
			IntentFilter filter = new IntentFilter();
			//isn't changed too often? (about 1 min)
	        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
	        filter.addAction(Intent.ACTION_BATTERY_LOW);
	        filter.addAction(Intent.ACTION_BATTERY_OKAY);
	        filter.addAction(Intent.ACTION_POWER_CONNECTED);
	        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
	        
	        this.mContext.registerReceiver(this, filter);
	        
	        this.regBattery = true;
		}
	}

	@Override
	public void unregister() {
		
		if (this.regBattery){
			
			this.mContext.unregisterReceiver(this);			
			this.regBattery = false;
		}
		mContext.deleteRecurringRequests(getDataType());
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String getDataType() {
		
		return DataCollectService.BATTERY;
	}

}
