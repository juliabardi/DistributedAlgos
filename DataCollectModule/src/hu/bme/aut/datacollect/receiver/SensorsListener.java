package hu.bme.aut.datacollect.receiver;

import hu.bme.aut.datacollect.db.DataCollectDao;

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
	
	private final float NOISE = 2.0F;
	
	private final Context mContext;
	
	private DataCollectDao dao;
	
	private Acceleration currentAcc = null;
	
	public SensorsListener(Context context){
		
		mContext = context;
		
		//setup dao
		dao = DataCollectDao.getInstance(mContext);
		
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
			
			Acceleration acc = new Acceleration(accX, accY, accZ);
			
			//if differs from previous, save (some noise is ok)
			if (currentAcc == null || !currentAcc.equalsWithNoise(acc)){
				//calculate eredo see dia if necessary			
				//save into db
				dao.insertAcceleration(Calendar.getInstance().getTimeInMillis(), accX, accY, accZ);
				currentAcc = acc;
			}

		}
		else if (event.sensor.equals(lightSensor)){
			
			//illuminance in lx
			float lux = event.values[0];			
			//save into db
			dao.insertLight(Calendar.getInstance().getTimeInMillis(), lux);
		}
		else if (event.sensor.equals(ambientTempSensor)){
			
			//ambient air temperature in C degrees.
			float temperature = event.values[0];			
			//save into db
			dao.insertAmbientTemperature(Calendar.getInstance().getTimeInMillis(), temperature);
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
	
	private class Acceleration {
		
		float accX;
		float accY;
		float accZ;
		
		public Acceleration(float accX, float accY, float accZ) {
			this.accX = accX;
			this.accY = accY;
			this.accZ = accZ;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + Float.floatToIntBits(accX);
			result = prime * result + Float.floatToIntBits(accY);
			result = prime * result + Float.floatToIntBits(accZ);
			return result;
		}
		
		public boolean equalsWithNoise(Acceleration acc){
			if (Math.abs(accX - acc.accX) < NOISE
					&& Math.abs(accY- acc.accY) < NOISE
					&& Math.abs(accZ - acc.accZ) < NOISE) {
				return true;
			}
			return false;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Acceleration other = (Acceleration) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (Float.floatToIntBits(accX) != Float.floatToIntBits(other.accX))
				return false;
			if (Float.floatToIntBits(accY) != Float.floatToIntBits(other.accY))
				return false;
			if (Float.floatToIntBits(accZ) != Float.floatToIntBits(other.accZ))
				return false;
			return true;
		}

		private SensorsListener getOuterType() {
			return SensorsListener.this;
		}
		
	}

}
