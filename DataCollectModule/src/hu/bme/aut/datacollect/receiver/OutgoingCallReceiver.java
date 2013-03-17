package hu.bme.aut.datacollect.receiver;

import hu.bme.aut.datacollect.db.DataCollectDao;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.GsmCellLocation;

public class OutgoingCallReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle extras = intent.getExtras();
		
		if (extras == null){
			return;
		}
		
		//get phone number
		//String phoneNumber = extras.getString(Intent.EXTRA_PHONE_NUMBER);
		
		//get dao
		DataCollectDao dao = DataCollectDao.getInstance(context);
		
		//get location
		GsmCellLocation location = LocationProvider.getGsmCellLocation(context);
		int lac = 0;
		if (location != null) {
			lac = location.getLac();
		}
		dao.insertCall(Calendar.getInstance().getTimeInMillis(), "out", lac);
	}

}
