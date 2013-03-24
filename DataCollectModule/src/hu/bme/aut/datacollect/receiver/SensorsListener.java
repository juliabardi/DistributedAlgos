package hu.bme.aut.datacollect.receiver;

import hu.bme.aut.datacollect.db.AccelerationDao;
import hu.bme.aut.datacollect.db.LightDao;
import hu.bme.aut.datacollect.db.TemperatureDao;
import hu.bme.aut.datacollect.entity.AccelerationData;
import hu.bme.aut.datacollect.entity.LightData;
import hu.bme.aut.datacollect.entity.TemperatureData;

import java.util.Calendar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorsListener implements SensorEventListener {
	
	public enum Sensors { ACCELEROMETER, LIGHT, TEMPERATURE };
	
	private final SensorManager sensorManager;
	private final Sensor accelerometerSensor;
	private final Sensor lightSensor;
	private final Sensor ambientTempSensor;
	
	private AccelerationDao accelerationDao = null;
	private LightDao lightDao = null;
	private TemperatureDao temperatureDao = null;
	
	private AccelerationData currentAcc = null;
	
	public SensorsListener(Context context, AccelerationDao aDao,
			LightDao lDao, TemperatureDao tDao) {

		//setup daos
		this.accelerationDao = aDao;
		this.lightDao = lDao;
		this.temperatureDao = tDao;
		
		//get sensor manager
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		
		//get sensors
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		//warning: api level 14
		ambientTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		//in case of accelerometer
		if (event.sensor.equals(accelerometerSensor)){
			
			float accX = event.values[0];
			float accY = event.values[1];
			float accZ = event.values[2];
			
			AccelerationData acc = new AccelerationData(0, accX, accY, accZ);
			
			//if differs from previous, save (some noise is ok)
			if (currentAcc == null || !currentAcc.equalsWithNoise(acc)){	
				// save into db
				accelerationDao.create(new AccelerationData(Calendar
						.getInstance().getTimeInMillis(), accX, accY, accZ));
				currentAcc = acc;
			}

		}
		else if (event.sensor.equals(lightSensor)){
			
			//illuminance in lx
			float lux = event.values[0];			
			//save into db
			lightDao.create(new LightData(Calendar.getInstance().getTimeInMillis(), lux));
		}
		else if (event.sensor.equals(ambientTempSensor)){
			
			//ambient air temperature in C degrees.
			float temperature = event.values[0];			
			//save into db
			temperatureDao.create(new TemperatureData(Calendar.getInstance()
					.getTimeInMillis(), temperature));
		}

	}

	public void registerListener(Sensors name) {
		// register listener for sensors
		if (name.equals(Sensors.ACCELEROMETER) && accelerometerSensor != null) {
			sensorManager.registerListener(this, accelerometerSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		} else if (name.equals(Sensors.LIGHT) && lightSensor != null) {
			sensorManager.registerListener(this, lightSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		} else if (name.equals(Sensors.TEMPERATURE)
				&& ambientTempSensor != null) {
			sensorManager.registerListener(this, ambientTempSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	public void unregisterListener(){
		sensorManager.unregisterListener(this);
	}
	
	public void unregisterListener(Sensors name) {
		switch (name) {
		case ACCELEROMETER:
			sensorManager.unregisterListener(this, accelerometerSensor);
			break;
		case LIGHT:
			sensorManager.unregisterListener(this, lightSensor);
			break;
		case TEMPERATURE:
			sensorManager.unregisterListener(this, ambientTempSensor);
			break;
		}
	}
	
}
