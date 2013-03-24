package hu.bme.aut.datacollect.receiver;

import hu.bme.aut.datacollect.db.CallDao;
import hu.bme.aut.datacollect.entity.CallData;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.GsmCellLocation;

public class OutgoingCallReceiver extends BroadcastReceiver{
	
	private CallDao callDao = null;
	
	public OutgoingCallReceiver(CallDao callDao){
		this.callDao = callDao;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle extras = intent.getExtras();
		
		if (extras == null){
			return;
		}
		
		//get location
		GsmCellLocation location = LocationProvider.getGsmCellLocation(context);
		int lac = 0;
		if (location != null) {
			lac = location.getLac();
		}
		callDao.create(new CallData(Calendar.getInstance().getTimeInMillis(), "out", lac));
	}

}
