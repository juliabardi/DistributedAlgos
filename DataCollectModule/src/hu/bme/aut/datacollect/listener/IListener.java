package hu.bme.aut.datacollect.listener;

public interface IListener {

	public void register();
	
	public void unregister();
	
	//determining if the current feature is available in the current device
	public boolean isAvailable();
	
	public String getDataType();
}
