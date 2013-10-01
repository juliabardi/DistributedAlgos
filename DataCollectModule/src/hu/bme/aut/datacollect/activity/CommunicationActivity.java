package hu.bme.aut.datacollect.activity;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import hu.bme.aut.communication.CommunicationService;
import hu.bme.aut.communication.CommunicationService.CommServiceBinder;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;
/**
 * Information for the user about the running service values.
 * @author Eva Pataji
 *
 */
// TODO Update data on server events.
public class CommunicationActivity extends Activity {
	private CommunicationService commService;
	private boolean commBound = false;
	TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tv = new TextView(this);
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (isServiceRunning(CommunicationService.class.getName())){
			Intent commIntent = new Intent(this,CommunicationService.class); 
			bindService(commIntent, commConnection, 0);			
		}
		else{
			TextView tv = new TextView(this);
			tv.setText("A kommunikációs modul inaktív.");
			setContentView(tv);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		 if (commBound) {
	            unbindService(commConnection);
	            commBound = false;
	        }
	}

	private boolean isServiceRunning(String serviceName) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceName.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}


	private ServiceConnection commConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CommServiceBinder binder = (CommServiceBinder) service;
            commService = binder.getService();
            commBound = true;
            StringBuilder builder = new StringBuilder();
            builder.append("GCM registration(got ID from service): "+commService.getRegisteredtoGCM().toString()+"\n" +
            		"GCM server connection: "+commService.getRegisteredtoServer().toString()+"\n" +
    				"Distributed Algos server connection: "+commService.getregisteredToDistributedAlgos().toString()+"\n");
    		HashMap<String, CommunicationService.SyncronizationValues> syncInfo=commService.getOfferSyncronizationInfo();
    		builder.append("\n");
    		builder.append("Offer Items | Server synced");
    		for (Map.Entry<String, CommunicationService.SyncronizationValues> entry : syncInfo.entrySet()) {
    			builder.append("\n"); 
    			builder.append("Key = " + entry.getKey() + ", Value = " + entry.getValue()); }
    		builder.append("\n");
    		tv.setText(builder.toString());
    		setContentView(tv);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            commBound = false;
        }
    };
	

}
