package hu.bme.aut.datacollect.receiver;

import hu.bme.aut.datacollect.db.LightDao;
import hu.bme.aut.datacollect.entity.LightData;

import java.util.Calendar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class LightSensorListener extends SensorsListener {

	private final Sensor lightSensor;
	private LightDao lightDao = null;

	public LightSensorListener(Context context, LightDao lDao) {
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
			lightDao.create(new LightData(Calendar.getInstance()
					.getTimeInMillis(), lux));
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

}
