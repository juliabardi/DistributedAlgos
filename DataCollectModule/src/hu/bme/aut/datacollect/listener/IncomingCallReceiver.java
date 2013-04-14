package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.CallData;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class IncomingCallReceiver extends BroadcastReceiver implements IListener{

	private DaoBase<CallData> callDao = null;
	private Context context = null;
	
	private boolean regIncoming = false;
	
	public IncomingCallReceiver(Context context, DaoBase<CallData> callDao){
		this.context = context;
		this.callDao = callDao;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle extras = intent.getExtras();
		
		if (extras == null){
			return;
		}
		
		String callState = extras.getString(TelephonyManager.EXTRA_STATE);
		
		if (TelephonyManager.EXTRA_STATE_RINGING.equals(callState)){			
			
			callDao.create(new CallData(Calendar.getInstance().getTimeInMillis(), "in"));
		}
	}

	
	@Override
	public void register() {
		
		if (!regIncoming){
			IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
			context.registerReceiver(this, intentFilter);
			regIncoming = true;
		}
	}
	
	@Override
	public void unregister() {
		
		if (regIncoming){
			context.unregisterReceiver(this);
			regIncoming = false;
		}
	}

}
