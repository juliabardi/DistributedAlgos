package hu.bme.aut.datacollect.receiver;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class SensorsListener implements SensorEventListener, IListener{
	
	public enum Sensors { ACCELEROMETER, LIGHT, TEMPERATURE };
	
	protected final SensorManager sensorManager;
	
	public SensorsListener(Context context) {
		//get sensor manager
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);		
	}
	
}
