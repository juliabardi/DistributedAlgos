package hu.bme.aut.datacollect.receiver;

import hu.bme.aut.datacollect.db.CallDao;
import hu.bme.aut.datacollect.entity.CallData;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class IncomingCallReceiver extends BroadcastReceiver{

	private CallDao callDao = null;
	
	public IncomingCallReceiver(CallDao callDao){
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
			//get location
			GsmCellLocation location = LocationProvider.getGsmCellLocation(context);
			int lac = 0;
			if (location != null) {
				lac = location.getLac();
			}
			callDao.create(new CallData(Calendar.getInstance().getTimeInMillis(), "in", lac));
		}
	}

}
