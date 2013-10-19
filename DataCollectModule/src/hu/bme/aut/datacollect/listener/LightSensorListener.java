package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.LightData;

import java.util.Calendar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class LightSensorListener extends SensorListener {
	
	private static final String TAG = "DataCollect:LightSensorListener";

	private final Sensor lightSensor;
	private DaoBase<LightData> lightDao = null;

	public LightSensorListener(DataCollectService context, DaoBase<LightData> lDao) {
		super(context);
		this.lightDao = lDao;

		lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.equals(lightSensor)) {

			// illuminance in lx
			float lux = event.values[0];
			// save into db
			Log.d(TAG, String.format("LightData: %s lx", lux));
			lightDao.create(new LightData(Calendar.getInstance()
					.getTimeInMillis(), lux));
			
			this.mContext.sendRecurringRequests();
		}

	}

	@Override
	public void register() {
		if (lightSensor != null) {
			sensorManager.registerListener(this, lightSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void unregister() {
		sensorManager.unregisterListener(this, lightSensor);
	}

	@Override
	public boolean isAvailable() {
		if (this.lightSensor != null){
			return true;
		}
		return false;
	}

	@Override
	public String getDataType() {
		return DataCollectService.LIGHT;
	}
}
