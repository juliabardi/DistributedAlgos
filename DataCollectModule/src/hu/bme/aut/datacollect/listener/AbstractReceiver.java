package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;
import android.content.BroadcastReceiver;

public abstract class AbstractReceiver extends BroadcastReceiver implements IListener{

	protected DataCollectService mContext;
	
	public AbstractReceiver(DataCollectService context){
		this.mContext = context;
	}
}
