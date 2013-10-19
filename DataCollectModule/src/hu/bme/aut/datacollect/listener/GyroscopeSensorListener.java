package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.GyroscopeData;

import java.util.Calendar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class GyroscopeSensorListener extends SensorListener {

	private static final String TAG = "DataCollect:GyroscopeSensorListener";
	
	private final Sensor gyroscopeSensor;
	private DaoBase<GyroscopeData> gyroscopeDao = null;
	
	public GyroscopeSensorListener(DataCollectService context, DaoBase<GyroscopeData> gyroscopeDao) {
		super(context);
		this.gyroscopeDao = gyroscopeDao;
		
		this.gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.equals(gyroscopeSensor)) {
			Log.d(TAG, String.format("GyroscopeData: axisX: %s, axisY: %s, axisZ: %s", 
					event.values[0], event.values[1], event.values[2]));
			gyroscopeDao.create(new GyroscopeData(Calendar.getInstance()
					.getTimeInMillis(), event.values[0], event.values[1],
					event.values[2]));
			
			this.mContext.sendRecurringRequests();
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

	@Override
	public boolean isAvailable() {
		if (this.gyroscopeSensor != null){
			return true;
		}
		return false;
	}

	@Override
	public String getDataType() {
		return DataCollectService.GYROSCOPE;
	}
}