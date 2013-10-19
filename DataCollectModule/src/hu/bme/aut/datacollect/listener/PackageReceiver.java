package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.PackageData;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class PackageReceiver extends AbstractReceiver {

	private static final String TAG = "DataCollect:PackageReceiver";
	
	private DaoBase<PackageData> packageDao = null;
	
	private boolean regPackage = false;
	
	public PackageReceiver(DataCollectService context, DaoBase<PackageData> packageDao) {
		super(context);
		this.packageDao = packageDao;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		int uid = intent.getExtras().getInt("EXTRA_UID");	
		Log.d(TAG, String.format("PackageData: action: %s, uid: %s", intent.getAction(), uid));
		packageDao.create(new PackageData(Calendar.getInstance().getTimeInMillis(), intent.getAction(), uid));
	
		this.mContext.sendRecurringRequests();
	}
	
	@Override
	public void register() {

		if (!this.regPackage){
			IntentFilter filter = new IntentFilter();
	        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
	        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
	        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
	        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
	        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
	        filter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
	        filter.addDataScheme("package");
	        
	        this.mContext.registerReceiver(this, filter);
	        
	        this.regPackage = true;
		}

	}

	@Override
	public void unregister() {
		
		if (this.regPackage){
			this.mContext.unregisterReceiver(this);
			this.regPackage = false;
		}
	}
	
	@Override
	public boolean isAvailable() {
		//receiving packages is available on every device
		return true;
	}

	@Override
	public String getDataType() {
		return DataCollectService.PACKAGE;
	}
}
