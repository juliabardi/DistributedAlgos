package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.ProximityData;

import java.util.Calendar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class ProximitySensorListener extends SensorListener {
	
	private static final String TAG = "DataCollect:ProximitySensorListener";

	private final Sensor proximitySensor;
	private DaoBase<ProximityData> proximityDao = null;

	public ProximitySensorListener(DataCollectService context, DaoBase<ProximityData> dao) {
		super(context);
		this.proximityDao = dao;

		proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.equals(proximitySensor)) {

			float distance = event.values[0];
			
			Log.d(TAG, String.format("ProximityData: %s cm", distance));
			proximityDao.create(new ProximityData(Calendar.getInstance()
					.getTimeInMillis(), distance));
			
			this.mContext.sendRecurringRequests();
		}

	}

	@Override
	public void register() {
		if (proximitySensor != null) {
			sensorManager.registerListener(this, proximitySensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void unregister() {
		sensorManager.unregisterListener(this, proximitySensor);
	}

	@Override
	public boolean isAvailable() {
		if (this.proximitySensor != null){
			return true;
		}
		return false;
	}

	@Override
	public String getDataType() {
		return DataCollectService.PROXIMITY;
	}
}
