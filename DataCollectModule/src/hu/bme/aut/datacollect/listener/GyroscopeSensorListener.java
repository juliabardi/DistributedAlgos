package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.GyroscopeData;

import java.util.Calendar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class GyroscopeSensorListener extends SensorListener {

	private final Sensor gyroscopeSensor;
	private DaoBase<GyroscopeData> gyroscopeDao = null;
	
	public GyroscopeSensorListener(Context context, DaoBase<GyroscopeData> gyroscopeDao) {
		super(context);
		this.gyroscopeDao = gyroscopeDao;
		
		this.gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.equals(gyroscopeSensor)) {
			gyroscopeDao.create(new GyroscopeData(Calendar.getInstance()
					.getTimeInMillis(), event.values[0], event.values[1],
					event.values[2]));
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void register() {
		if (gyroscopeSensor != null) {
			sensorManager.registerListener(this, gyroscopeSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void unregister() {
		sensorManager.unregisterListener(this, gyroscopeSensor);
	}

}