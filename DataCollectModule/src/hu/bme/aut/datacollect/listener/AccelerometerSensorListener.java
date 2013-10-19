package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.AccelerationData;

import java.util.Calendar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerSensorListener extends SensorListener {

	private static final String TAG = "DataCollect:AccelerometerSensorListener";
	
	private final Sensor accelerometerSensor;
	private DaoBase<AccelerationData> accelerationDao = null;

	private AccelerationData currentAcc = null;

	public AccelerometerSensorListener(DataCollectService context, DaoBase<AccelerationData> aDao) {
		super(context);
		this.accelerationDao = aDao;

		// get sensors
		accelerometerSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// in case of accelerometer
		if (event.sensor.equals(accelerometerSensor)) {

			float accX = event.values[0];
			float accY = event.values[1];
			float accZ = event.values[2];

			AccelerationData acc = new AccelerationData(0, accX, accY, accZ);

			// if differs from previous, save (some noise is ok)
			if (currentAcc == null || !currentAcc.equalsWithNoise(acc)) {
				// if (currentAcc == null || !currentAcc.equals(acc)){
				// save into db
				Log.d(TAG, String.format("AccelerationData: accX: %s, accY: %s, accZ: %s", accX, accY, accZ));
				accelerationDao.create(new AccelerationData(Calendar
						.getInstance().getTimeInMillis(), accX, accY, accZ));
				currentAcc = acc;
				
				this.mContext.sendRecurringRequests();
			}
		}
	}

	@Override
	public void register() {
		// register listener for sensor
		if (accelerometerSensor != null) {
			sensorManager.registerListener(this, accelerometerSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void unregister() {
		sensorManager.unregisterListener(this, accelerometerSensor);
	}

	@Override
	public boolean isAvailable() {
		if (this.accelerometerSensor != null){
			return true;
		}
		return false;
	}

	@Override
	public String getDataType() {
		
		return DataCollectService.ACCELERATION;
	}

}
