package hu.bme.aut.datacollect.listener;

import hu.bme.aut.datacollect.activity.DataCollectService;

public abstract class AbstractListener implements IListener {

	protected DataCollectService mContext;
	
	public AbstractListener(DataCollectService context){
		this.mContext = context;
	}

}
