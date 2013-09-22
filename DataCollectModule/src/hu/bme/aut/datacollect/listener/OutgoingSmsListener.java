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

public class OutgoingSmsListener extends ContentObserver implements IListener {

	private DaoBase<SmsData> smsDao = null;
	private Context context = null;
	
	private boolean regOutSms = false;
	
	private int lastId = 0;
		
	public OutgoingSmsListener(Context context, Handler handler, DaoBase<SmsData> smsDao){
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
	    		smsDao.create(new SmsData(Calendar.getInstance().getTimeInMillis(), "out"));
	        }               
	        else {
	             //the message is received just now   
	        	 //this is handled with IncomingSmsReceiver
	        }
        }
	}

	@Override
	public void register() {

		if (!regOutSms) {
			ContentResolver contentResolver = context.getContentResolver();
			contentResolver.registerContentObserver(
					Uri.parse("content://sms"), true, this);
			regOutSms = true;
		}
	}
	
	@Override
	public void unregister(){
		
		if (regOutSms){
			ContentResolver contentResolver = context.getContentResolver();
			contentResolver.unregisterContentObserver(this);
			regOutSms = false;
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
