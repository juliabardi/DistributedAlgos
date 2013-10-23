package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.CallData;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends AbstractReceiver {
	
	private static final String TAG = "DataCollect:CallReceiver";

	private DaoBase<CallData> callDao = null;
	
	private boolean regCall = false;
	
	public CallReceiver(DataCollectService mContext, DaoBase<CallData> callDao){
		super(mContext);
		this.callDao = callDao;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())){
			
			Log.d(TAG, String.format("CallData: out"));
			callDao.create(new CallData(Calendar.getInstance().getTimeInMillis(), "out"));
		}		
		else {  //must be incoming call
			Bundle extras = intent.getExtras();
			
			if (extras == null){
				return;
			}			
			String callState = extras.getString(TelephonyManager.EXTRA_STATE);
			
			if (TelephonyManager.EXTRA_STATE_RINGING.equals(callState)){			
				Log.d(TAG, String.format("CallData: in"));
				callDao.create(new CallData(Calendar.getInstance().getTimeInMillis(), "in"));
			}
		}
		
		this.mContext.sendRecurringRequests();
	}

	
	@Override
	public void register() {
		
		if (!regCall){
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
			intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
			mContext.registerReceiver(this, intentFilter);
			regCall = true;
		}
	}
	
	@Override
	public void unregister() {
		
		if (regCall){
			mContext.unregisterReceiver(this);
			regCall = false;
		}
		mContext.deleteRecurringRequests(getDataType());
	}

	@Override
	public boolean isAvailable() {
		
		TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
		if (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT){
			return true;
		}
		return false;
	}

	@Override
	public String getDataType() {
		return DataCollectService.CALL;
	}

}
