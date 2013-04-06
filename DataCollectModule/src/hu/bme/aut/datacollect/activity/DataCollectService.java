package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.entity.AccelerationData;
import hu.bme.aut.datacollect.entity.CallData;
import hu.bme.aut.datacollect.entity.LightData;
import hu.bme.aut.datacollect.entity.LocationData;
import hu.bme.aut.datacollect.entity.SmsData;
import hu.bme.aut.datacollect.entity.TemperatureData;
import hu.bme.aut.datacollect.receiver.AccelerometerSensorListener;
import hu.bme.aut.datacollect.receiver.IListener;
import hu.bme.aut.datacollect.receiver.IncomingCallReceiver;
import hu.bme.aut.datacollect.receiver.IncomingSmsReceiver;
import hu.bme.aut.datacollect.receiver.LightSensorListener;
import hu.bme.aut.datacollect.receiver.LocationProvider;
import hu.bme.aut.datacollect.receiver.OutgoingCallReceiver;
import hu.bme.aut.datacollect.receiver.OutgoingSmsListener;
import hu.bme.aut.datacollect.receiver.TemperatureSensorListener;

import java.util.HashMap;
import java.util.Map;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;

public class DataCollectService extends OrmLiteBaseService<DatabaseHelper> {

	private final ServiceBinder mBinder = new ServiceBinder();

	private Map<String, IListener> listeners = new HashMap<String, IListener>();

	public IListener getListener(String key) {
		return listeners.get(key);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// instantiate class, it will register for loc updates
		listeners.put("location", new LocationProvider(this, getHelper()
				.getDaoBase(LocationData.class)));

		// instantiate to register
		listeners.put("acceleration", new AccelerometerSensorListener(this,
				getHelper().getDaoBase(AccelerationData.class)));
		listeners.put("light", new LightSensorListener(this, getHelper()
				.getDaoBase(LightData.class)));
		listeners.put("temperature", new TemperatureSensorListener(this,
				getHelper().getDaoBase(TemperatureData.class)));

		listeners.put("incall", new IncomingCallReceiver(this, getHelper()
				.getDaoBase(CallData.class)));
		listeners.put("outcall", new OutgoingCallReceiver(getHelper()
				.getDaoBase(CallData.class)));

		listeners.put("insms", new IncomingSmsReceiver(this, getHelper()
				.getDaoBase(SmsData.class)));
		listeners.put("outsms", new OutgoingSmsListener(this, new Handler(),
				getHelper().getDaoBase(SmsData.class)));

		// get the current settings
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		// register all listeners that are enabled
		for (String key : MainActivity.sharedPrefKeys) {
			if (sharedPreferences.getBoolean(key, false))
				listeners.get(key).register();
		}

		this.setupForeground();
	}

	private void setupForeground() {

		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("DataCollectModule")
				.setContentText("Adatgyûjtés folyamatban.").setOngoing(true)
				.setContentIntent(pendingIntent);

		this.startForeground(1, builder.build());
	}

	@Override
	public void onDestroy() {

		// unregister stuff if necessary
		for (String key : listeners.keySet()) {
			listeners.get(key).unregister();
		}
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

		DataCollectService getService() {
			return DataCollectService.this;
		}
	}

}
