package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.SmsData;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

public class IncomingSmsReceiver extends BroadcastReceiver implements IListener {

	private DaoBase<SmsData> smsDao = null;
	private Context mContext = null;
	
	private boolean regInSms = false;
	
	public IncomingSmsReceiver(Context context, DaoBase<SmsData> smsDao){
		this.smsDao = smsDao;
		this.mContext = context;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())){
			//save into db
			smsDao.create(new SmsData(Calendar.getInstance().getTimeInMillis(), "in"));
		}
	}
	
	@Override
	public void register(){
		
		if (!regInSms){
			IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
			mContext.registerReceiver(this, intentFilter);
			regInSms = true;
		}
	}
	
	@Override
	public void unregister(){
		
		if (regInSms){
			mContext.unregisterReceiver(this);
			regInSms = false;
		}
	}

	@Override
	public boolean isAvailable() {
		
		TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
		if (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT){
			return true;
		}
		return false;
	}
}
