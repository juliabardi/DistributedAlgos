package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.SmsData;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SmsListener extends ContentObserver implements IListener {
	
	private static final String TAG = "DataCollect:SmsListener";

	private DaoBase<SmsData> smsDao = null;
	private Context context = null;
	
	private boolean regSms = false;
	
	private int lastId = 0;
		
	public SmsListener(Context context, Handler handler, DaoBase<SmsData> smsDao){
		super(handler);
		this.smsDao = smsDao;
		this.context = context;
	}
	
	@Override
	public void onChange(boolean selfChange, Uri uri) {
		
        Uri uriSMSURI = Uri.parse("content://sms");
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null,
             null, null);
        cur.moveToNext();
        
        if (this.lastId != cur.getInt(cur.getColumnIndex("_id"))){
        	//last id is needed because the method gets called 3 times
        	this.lastId = cur.getInt(cur.getColumnIndex("_id"));
	        String protocol = cur.getString(cur.getColumnIndex("protocol"));
	
	        if(protocol == null) {
	        	//the message is sent out just now  
				Log.d(TAG, "SmsData: out");
	    		smsDao.create(new SmsData(Calendar.getInstance().getTimeInMillis(), "out"));
	        }               
	        else {
	             //the message is received just now   
				Log.d(TAG, "SmsData: in");
				smsDao.create(new SmsData(Calendar.getInstance().getTimeInMillis(), "in"));
	        }
        }
	}

	@Override
	public void register() {

		if (!regSms) {
			ContentResolver contentResolver = context.getContentResolver();
			contentResolver.registerContentObserver(
					Uri.parse("content://sms"), true, this);
			regSms = true;
		}
	}
	
	@Override
	public void unregister(){
		
		if (regSms){
			ContentResolver contentResolver = context.getContentResolver();
			contentResolver.unregisterContentObserver(this);
			regSms = false;
		}
	}
	
	@Override
	public boolean isAvailable() {
		
		TelephonyManager tm = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
		if (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT){
			return true;
		}
		return false;
	}
}
