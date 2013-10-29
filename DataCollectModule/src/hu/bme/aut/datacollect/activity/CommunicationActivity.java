package hu.bme.aut.datacollect.activity;

import hu.bme.aut.communication.CommunicationService;
import hu.bme.aut.communication.CommunicationService.CommServiceBinder;
import hu.bme.aut.communication.CommunicationService.CommunicationListener;
import hu.bme.aut.communication.CommunicationService.SyncronizationValues;

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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Information for the user about the running service values.
 * @author Eva Pataji
 *
 */
// TODO Update data on server events.
public class CommunicationActivity extends Activity implements CommunicationListener {
	private CommunicationService commService;
	private boolean commBound = false;
	private LinearLayout layoutCollectedData;
	private CheckedTextView chtvGCMReg;
	private CheckedTextView chtvGCMConn;
	private TextView chtvGCMConnSending;
	private CheckedTextView chtvNodeConn;
	private TextView chtvNodeConnSending;
	private LinearLayout.LayoutParams paramLayout;
	private LinearLayout.LayoutParams paramCell;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sync_layout);
		layoutCollectedData = (LinearLayout)findViewById(R.id.offerTable);
    	chtvGCMReg = (CheckedTextView)findViewById(R.id.checkedGCMReg);
    	chtvGCMConn = (CheckedTextView)findViewById(R.id.checkedGCMServer);
    	chtvGCMConnSending = (TextView)findViewById(R.id.checkedGCMSending);
    	chtvNodeConn = (CheckedTextView)findViewById(R.id.checkedDistAlgos);
    	chtvNodeConnSending = (TextView)findViewById(R.id.checkedGDistAlgosSending);
    	paramLayout = new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.MATCH_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT);
    	paramCell = new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.MATCH_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
    	paramCell.leftMargin=5;
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
		if (commBound) {
			commService.unRegisterListener();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (commBound) {
			commService.registerListener(this);
		}
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
            commService.registerListener(CommunicationActivity.this);
    		setupData();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            commBound = false;
        }
    };
    
    private void setupData(){
 		updateGCMServerData();
 		updateCollectedData();
    }
    
    private void updateCollectedData(){
    	layoutCollectedData.removeAllViews();
    	
    	for (Map.Entry<String, CommunicationService.SyncronizationValues> entry : commService.getOfferSyncronizationInfo().entrySet()){
			LinearLayout tr = new LinearLayout(this);
			tr.setLayoutParams(paramLayout);
			tr.setOrientation(LinearLayout.HORIZONTAL);
			TextView view = new TextView(this);
			view.setText(entry.getKey());
			view.setLayoutParams(paramCell);
			tr.addView(view);
			view = new TextView(this);
			view.setText(entry.getValue().toString());
			view.setLayoutParams(paramCell);
			tr.addView(view);
			layoutCollectedData.addView(tr);
		}
    }
    
   private void updateDistAlgosServerData(){
	   if(commService.getregisteredToDistributedAlgos() == SyncronizationValues.TRUE){	
	   		chtvNodeConn.setChecked(true);
		}else{
			chtvNodeConn.setChecked(false);
			if(commService.getregisteredToDistributedAlgos() == SyncronizationValues.SENDING){
				chtvNodeConnSending.setVisibility(View.VISIBLE); return;
				}
			
		}
  		chtvNodeConnSending.setVisibility(View.INVISIBLE);
   }
   
   private void updateGCMServerData(){
		chtvGCMReg.setChecked(commService.getRegisteredtoGCMService());
		if(commService.getRegisteredtoGCMServer() == SyncronizationValues.TRUE){
			chtvGCMConn.setChecked(true);
		}else{
			chtvGCMConn.setChecked(false);
			if(commService.getRegisteredtoGCMServer() == SyncronizationValues.SENDING){
				chtvGCMConnSending.setVisibility(View.VISIBLE); return;
				}
		}
		chtvGCMConnSending.setVisibility(View.INVISIBLE);
   }
    
    private Boolean needSyncState(){
    	for (Map.Entry<String, CommunicationService.SyncronizationValues> entry : commService.getOfferSyncronizationInfo().entrySet()){
    		if(entry.getValue() == CommunicationService.SyncronizationValues.FALSE
    				|| entry.getValue() == CommunicationService.SyncronizationValues.NONE){
    			return true;
    		}
    	}
    	return false;
    }
    
    
    public void  navigateToRequests(View v){
    	Toast.makeText(getApplication(), "Hamarosan jövünk!", Toast.LENGTH_SHORT).show();   	
    }
    
    public void syncCollectedDataStates(View v){
    	if(commBound){
    		if(needSyncState()){
    			commService.syncCollectedDataStates();
    		}else Toast.makeText(getApplication(), "Nincs mit szinkronizálni.", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void connectToGCMServer(View v){
    	if(commBound){
    		if(commService.reConnectToGCM()){
    			Toast.makeText(getApplicationContext(), "Csatlakozás folyamatban...", Toast.LENGTH_SHORT).show();
    		}
    		
    		updateGCMServerData();
    	}
    }
    
    public void connectToDistAlgoServer(View v){
    	if(commBound){
    		if(commService.reConnectToDistAlgos()){
    			Toast.makeText(getApplicationContext(), "Csatlakozás folyamatban...", Toast.LENGTH_SHORT).show();
    		}
    		
    		updateDistAlgosServerData();
    	}
    }

	@Override
	public void refreshCollectedDataStates() {
		Log.i(this.getClass().getName(), "Updating Node Server info and Collected Data States.");
		updateDistAlgosServerData();
		updateCollectedData();
	}

	@Override
	public void refreshGCMConnData() {
		Log.i(this.getClass().getName(), "Updating GCM Server info.");
		updateGCMServerData();
	}
	

}
