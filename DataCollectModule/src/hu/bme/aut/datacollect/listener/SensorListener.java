package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class SensorListener extends AbstractListener implements SensorEventListener{
	
	protected final SensorManager sensorManager;
	
	public SensorListener(DataCollectService context) {
		super(context);
		//get sensor manager
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);		
	}
	
}
