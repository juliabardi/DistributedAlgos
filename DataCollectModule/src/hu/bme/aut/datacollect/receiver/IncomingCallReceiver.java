package hu.bme.aut.datacollect.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class IncomingCallReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle extras = intent.getExtras();
		
		if (extras == null){
			return;
		}
		
		String callState = extras.getString(TelephonyManager.EXTRA_STATE);
		if (TelephonyManager.EXTRA_STATE_RINGING.equals(callState)){
			String incomingNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
			
			//Do something
		}
	}

}
