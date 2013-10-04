package hu.bme.aut.datacollect.listener;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class SensorListener implements SensorEventListener, IListener{
	
	protected final SensorManager sensorManager;
	
	public SensorListener(Context context) {
		//get sensor manager
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);		
	}
	
}
