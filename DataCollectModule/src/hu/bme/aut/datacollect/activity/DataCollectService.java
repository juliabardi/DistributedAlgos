package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.entity.AccelerationData;
import hu.bme.aut.datacollect.entity.BatteryData;
import hu.bme.aut.datacollect.entity.CallData;
import hu.bme.aut.datacollect.entity.ConnectivityData;
import hu.bme.aut.datacollect.entity.GyroscopeData;
import hu.bme.aut.datacollect.entity.LightData;
import hu.bme.aut.datacollect.entity.LocationData;
import hu.bme.aut.datacollect.entity.PackageData;
import hu.bme.aut.datacollect.entity.ProximityData;
import hu.bme.aut.datacollect.entity.ScreenData;
import hu.bme.aut.datacollect.entity.SmsData;
import hu.bme.aut.datacollect.entity.TemperatureData;
import hu.bme.aut.datacollect.listener.AccelerometerSensorListener;
import hu.bme.aut.datacollect.listener.BatteryReceiver;
import hu.bme.aut.datacollect.listener.CallReceiver;
import hu.bme.aut.datacollect.listener.ConnectivityReceiver;
import hu.bme.aut.datacollect.listener.GyroscopeSensorListener;
import hu.bme.aut.datacollect.listener.IListener;
import hu.bme.aut.datacollect.listener.LightSensorListener;
import hu.bme.aut.datacollect.listener.LocationProvider;
import hu.bme.aut.datacollect.listener.PackageReceiver;
import hu.bme.aut.datacollect.listener.ProximitySensorListener;
import hu.bme.aut.datacollect.listener.ScreenReceiver;
import hu.bme.aut.datacollect.listener.SmsListener;
import hu.bme.aut.datacollect.listener.TemperatureSensorListener;

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
	
	public static final int IMAGE_NOTIF_ID = 100;
	
	//Cannot access the R.string.whatever from static place easily, so I keep here the strings
	public static final String ACCELERATION = "AccelerationData";
	public static final String LIGHT = "LightData";
	public static final String TEMPERATURE = "TemperatureData";
	public static final String GYROSCOPE = "GyroscopeData";
	public static final String LOCATION = "LocationData";
	public static final String CALL = "CallData";
	public static final String SMS = "SmsData";
	public static final String PACKAGE = "PackageData";
	public static final String CONNECTIVITY = "ConnectivityData";
	public static final String BATTERY = "BatteryData";
	public static final String PROXIMITY = "ProximityData";
	public static final String SCREEN = "ScreenData";
	
	public static final String IMAGE = "ImageData";	
	//no listeners yet
	public static final String ORIENTAION = "OrientationData";

	public static final String[] sharedPrefKeys = new String[] { ACCELERATION,
			LIGHT, TEMPERATURE, GYROSCOPE, LOCATION, CALL,
			SMS, PACKAGE, CONNECTIVITY, BATTERY, PROXIMITY, SCREEN, IMAGE};

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
		listeners.put(LOCATION, new LocationProvider(this, getHelper()
				.getDaoBase(LocationData.class)));

		// instantiate to register
		listeners.put(ACCELERATION, new AccelerometerSensorListener(this,
				getHelper().getDaoBase(AccelerationData.class)));
		listeners.put(LIGHT, new LightSensorListener(this, getHelper()
				.getDaoBase(LightData.class)));
		listeners.put(TEMPERATURE, new TemperatureSensorListener(this,
				getHelper().getDaoBase(TemperatureData.class)));
		listeners.put(GYROSCOPE, new GyroscopeSensorListener(this,
				getHelper().getDaoBase(GyroscopeData.class)));

		listeners.put(CALL, new CallReceiver(this, getHelper()
				.getDaoBase(CallData.class)));
		listeners.put(SMS, new SmsListener(this, new Handler(),
				getHelper().getDaoBase(SmsData.class)));
		
		listeners.put(PACKAGE, new PackageReceiver(this, getHelper().getDaoBase(PackageData.class)));
		listeners.put(CONNECTIVITY, new ConnectivityReceiver(this, getHelper().getDaoBase(ConnectivityData.class)));
		listeners.put(BATTERY, new BatteryReceiver(this, getHelper().getDaoBase(BatteryData.class)));
		listeners.put(PROXIMITY, new ProximitySensorListener(this, getHelper().getDaoBase(ProximityData.class)));
		listeners.put(SCREEN, new ScreenReceiver(this, getHelper().getDaoBase(ScreenData.class)));		
		
		// get the current settings
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		// register all listeners that are enabled (and has listener)
		for (String key : DataCollectService.sharedPrefKeys) {
			if (sharedPreferences.getBoolean(key, false) && listeners.get(key) != null &&
					listeners.get(key).isAvailable()){
				listeners.get(key).register();
			}				
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
			if (listeners.get(key).isAvailable()){
				listeners.get(key).unregister();
			}
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
