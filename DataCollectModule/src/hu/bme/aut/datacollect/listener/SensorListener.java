package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class SensorListener implements SensorEventListener, IListener{
	
	protected final SensorManager sensorManager;
	
	protected DataCollectService mContext;
	
	public SensorListener(DataCollectService context) {
		this.mContext = context;
		//get sensor manager
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);		
	}
	
}
