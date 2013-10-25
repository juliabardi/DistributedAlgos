package hu.bme.aut.datacollect.activity;

import hu.bme.aut.communication.CommunicationService;
import hu.bme.aut.communication.CommunicationService.CommServiceBinder;

import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.CheckedTextView;
import android.widget.TableLayout;
import android.widget.TableRow;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sync_layout);
		
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
			tv.setTextAppearance(this, android.R.style.TextAppearance_Large);
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
    		
    		CheckedTextView chtv = (CheckedTextView)findViewById(R.id.checkedGCMReg);
    		chtv.setChecked(commService.getRegisteredtoGCM());
    		chtv = (CheckedTextView)findViewById(R.id.checkedGCMServer);
    		chtv.setChecked(commService.getRegisteredtoServer());
    		chtv = (CheckedTextView)findViewById(R.id.checkedDistAlgos);
    		chtv.setChecked(commService.getregisteredToDistributedAlgos());
    		TableLayout table = (TableLayout)findViewById(R.id.offerTable);
    		
    		for (Map.Entry<String, CommunicationService.SyncronizationValues> entry : commService.getOfferSyncronizationInfo().entrySet()){
    			TableRow tr = new TableRow(CommunicationActivity.this);
    			TextView view = new TextView(CommunicationActivity.this);
    			view.setText(entry.getKey());
    			tr.addView(view);
    			view = new TextView(CommunicationActivity.this);
    			view.setText(entry.getValue().toString());
    			view.setPadding(20, 0, 0, 0);
    			tr.addView(view);
    			table.addView(tr);
    		}
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            commBound = false;
        }
    };
	

}
