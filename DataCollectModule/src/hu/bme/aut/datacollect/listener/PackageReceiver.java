package hu.bme.aut.datacollect.listener;

import java.util.Calendar;

import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.PackageData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class PackageReceiver extends BroadcastReceiver implements IListener {

	private DaoBase<PackageData> packageDao = null;
	private Context context = null;
	
	private boolean regPackage = false;
	
	public PackageReceiver(Context context, DaoBase<PackageData> packageDao) {
		super();
		this.packageDao = packageDao;
		this.context = context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		int uid = intent.getExtras().getInt("EXTRA_UID");		
		packageDao.create(new PackageData(Calendar.getInstance().getTimeInMillis(), intent.getAction(), uid));
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
	        
	        this.context.registerReceiver(this, filter);
	        
	        this.regPackage = true;
		}

	}

	@Override
	public void unregister() {
		
		if (this.regPackage){
			this.context.unregisterReceiver(this);
			this.regPackage = false;
		}
	}
}
