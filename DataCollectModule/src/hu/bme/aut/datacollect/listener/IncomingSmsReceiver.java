package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.SmsData;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class IncomingSmsReceiver extends BroadcastReceiver implements IListener {

	private DaoBase<SmsData> smsDao = null;
	private Context context = null;
	
	private boolean regInSms = false;
	
	public IncomingSmsReceiver(Context context, DaoBase<SmsData> smsDao){
		this.smsDao = smsDao;
		this.context = context;
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
			context.registerReceiver(this, intentFilter);
			regInSms = true;
		}
	}
	
	@Override
	public void unregister(){
		
		if (regInSms){
			context.unregisterReceiver(this);
			regInSms = false;
		}
	}

}