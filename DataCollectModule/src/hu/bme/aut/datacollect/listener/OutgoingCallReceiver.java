package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.CallData;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class OutgoingCallReceiver extends BroadcastReceiver implements IListener{
	
	private DaoBase<CallData> callDao = null;
	private Context context = null;
	
	private boolean regOutgoing = false;
	
	public OutgoingCallReceiver(Context context, DaoBase<CallData> callDao){
		this.callDao = callDao;
		this.context = context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
				
		callDao.create(new CallData(Calendar.getInstance().getTimeInMillis(), "out"));
	}
	
	@Override
	public void register(){
		
		if (!regOutgoing){
			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
			context.registerReceiver(this, intentFilter);
			regOutgoing = true;
		}
	}
	
	@Override
	public void unregister(){
		
		if (regOutgoing){
			context.unregisterReceiver(this);
			regOutgoing = false;
		}
	}

}
