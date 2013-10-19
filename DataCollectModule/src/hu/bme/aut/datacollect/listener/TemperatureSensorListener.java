package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.entity.TemperatureData;

import java.util.Calendar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class TemperatureSensorListener extends SensorListener {
	
	private static final String TAG = "DataCollect:TemperatureSensorListener";

	private final Sensor ambientTempSensor;
	private DaoBase<TemperatureData> temperatureDao = null;

	public TemperatureSensorListener(DataCollectService context, DaoBase<TemperatureData> tDao) {
		super(context);
		this.temperatureDao = tDao;

		// warning: api level 14
		ambientTempSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.equals(ambientTempSensor)) {

			// ambient air temperature in C degrees.
			float temperature = event.values[0];
			Log.d(TAG, String.format("TemperatureData: %s°C", temperature));
			temperatureDao.create(new TemperatureData(Calendar.getInstance()
					.getTimeInMillis(), temperature));
			
			this.mContext.sendRecurringRequests(this.getDataType());
		}
	}

	@Override
	public void register() {
		if (ambientTempSensor != null) {
			sensorManager.registerListener(this, ambientTempSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void unregister() {
		sensorManager.unregisterListener(this, ambientTempSensor);
	}
	
	@Override
	public boolean isAvailable() {
		if (this.ambientTempSensor != null){
			return true;
		}
		return false;
	}

	@Override
	public String getDataType() {
		return DataCollectService.TEMPERATURE;
	}
}
