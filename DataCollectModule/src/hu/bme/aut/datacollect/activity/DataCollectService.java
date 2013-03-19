package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.receiver.IncomingCallReceiver;
import hu.bme.aut.datacollect.receiver.LocationProvider;
import hu.bme.aut.datacollect.receiver.OutgoingCallReceiver;
import hu.bme.aut.datacollect.receiver.SensorsListener;
import hu.bme.aut.datacollect.receiver.SensorsListener.Sensors;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;

public class DataCollectService extends OrmLiteBaseService<DatabaseHelper> {
	
	private final ServiceBinder mBinder = new ServiceBinder();
	
	private LocationProvider locProvider;
	private SensorsListener sensorsListener;
	private IncomingCallReceiver incomingReceiver;
	private OutgoingCallReceiver outgoingReceiver;
	
	boolean regIncoming = false;
	boolean regOutgoing = false;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
        //instantiate class, it will register for loc updates
        locProvider = new LocationProvider(this, getHelper().getLocationDao());
        
        //instantiate to register
		sensorsListener = new SensorsListener(this, getHelper()
				.getAccelerationDao(), getHelper().getLightDao(), getHelper()
				.getTemperatureDao());
 
        incomingReceiver = new IncomingCallReceiver(getHelper().getCallDao());
        outgoingReceiver = new OutgoingCallReceiver(getHelper().getCallDao());
        
        //get the current settings
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (sharedPreferences.getBoolean("acceleration", false)) {
			this.registerSensorsListener(Sensors.ACCELEROMETER);
		}
		if (sharedPreferences.getBoolean("light", false)) {
			this.registerSensorsListener(Sensors.LIGHT);
		}
		if (sharedPreferences.getBoolean("temperature", false)) {
			this.registerSensorsListener(Sensors.TEMPERATURE);
		}
		if (sharedPreferences.getBoolean("location", false)) {
			this.registerLocationListener();
		}
		if (sharedPreferences.getBoolean("incoming", false)) {
			this.registerReceiverIncoming();
		}
		if (sharedPreferences.getBoolean("outgoing", false)) {
			this.registerReceiverOutgoing();

		}
		
		this.setupForeground();
	}
	
	private void setupForeground(){
		
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		
		Notification notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("DataCollectModule")
				.setContentText("Adatgyûjtés folyamatban.")
				.setOngoing(true)
				.setContentIntent(pendingIntent)
				.getNotification();

		this.startForeground(1, notification);
	}

	@Override
	public void onDestroy() {
		
		//unregister stuff if necessary
		locProvider.unregisterListener();
		sensorsListener.unregisterListener();
		this.unregisterReceiverIncoming();
		this.unregisterReceiverOutgoing();
		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	public class ServiceBinder extends Binder {
		
		DataCollectService getService(){
			return DataCollectService.this;
		}
	}
	
	public void registerReceiverIncoming(){
		
		if (!regIncoming){
			IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
			this.registerReceiver(incomingReceiver, intentFilter);
			regIncoming = true;
		}
	}
	
	public void unregisterReceiverIncoming(){
		
		if (regIncoming){
			this.unregisterReceiver(incomingReceiver);
			regIncoming = false;
		}
	}
	
	public void registerReceiverOutgoing(){
		
		if (!regOutgoing){
			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
			this.registerReceiver(outgoingReceiver, intentFilter);
			regOutgoing = true;
		}
	}
	
	public void unregisterReceiverOutgoing(){
		
		if (regOutgoing){
			this.unregisterReceiver(outgoingReceiver);
			regOutgoing = false;
		}
	}
	
	public void registerSensorsListener(Sensors name){
		sensorsListener.registerListener(name);
	}
	
	public void unregisterSensorsListener(Sensors name){
		sensorsListener.unregisterListener(name);
	}

	public void registerLocationListener(){
		locProvider.registerListener();
	}
	
	public void unregisterLocationListener(){
		locProvider.unregisterListener();
	}
}
