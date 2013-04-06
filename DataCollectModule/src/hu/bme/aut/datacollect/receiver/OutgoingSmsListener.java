package hu.bme.aut.datacollect.receiver;

import hu.bme.aut.datacollect.db.SmsDao;
import hu.bme.aut.datacollect.entity.SmsData;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class OutgoingSmsListener extends ContentObserver implements IListener {

	private SmsDao smsDao = null;
	private Context context = null;
	
	private boolean regOutSms = false;
	
	public OutgoingSmsListener(Handler handler) {
		super(handler);
	}
	
	public OutgoingSmsListener(Context context, Handler handler, SmsDao smsDao){
		super(handler);
		this.smsDao = smsDao;
		this.context = context;
	}

	@Override
	public void onChange(boolean selfChange) {
		
		smsDao.create(new SmsData(Calendar.getInstance().getTimeInMillis(), "out"));
		
		super.onChange(selfChange);
	}
	
	@Override
	public void register() {

		if (!regOutSms) {
			ContentResolver contentResolver = context.getContentResolver();
			contentResolver.registerContentObserver(
					Uri.parse("content://sms/out"), true, this);
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
}
