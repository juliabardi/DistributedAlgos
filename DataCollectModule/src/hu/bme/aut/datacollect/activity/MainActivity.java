package hu.bme.aut.datacollect.activity;

import hu.bme.aut.communication.CommunicationService;
import hu.bme.aut.communication.CommunicationService.CommServiceBinder;
import hu.bme.aut.communication.Constants;
import hu.bme.aut.datacollect.activity.DataCollectService.ServiceBinder;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.listener.IListener;
import hu.bme.aut.datacollect.upload.DataUploadTask;
import hu.bme.aut.datacollect.upload.UploadTaskQueue;
import hu.bme.aut.datacollect.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> implements
		OnSharedPreferenceChangeListener {
	
	private static final String TAG ="DataCollect:MainActivity";
	private static final int SENDER_COMMUNICATION=0;
	private static final int SENDER_WIFI=1;
	private SharedPreferences settings;
	
	private static final int REQUEST_CODE_OPTIONS = 0;
	
	private DataCollectService mService;
	private CommunicationService commService;
	private Intent intent = null;
	private Intent commIntent=null;
	private boolean mBound = false;
	private boolean commBound = false;
	private ToggleButton communicationButton;
	private ToggleButton measureButton;
	
	private UploadTaskQueue queue = UploadTaskQueue.instance(this);
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        communicationButton = (ToggleButton)findViewById(R.id.buttonCommunicationStop);
        measureButton = (ToggleButton)findViewById(R.id.buttonStop);
                        
		settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		settings.registerOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) this);
		
		intent = new Intent(this, DataCollectService.class);
		commIntent = new Intent(this,CommunicationService.class); 
		
		checkWifiAvaiable();
		
		// Start service if not already started.
		if (!isServiceRunning(DataCollectService.class.getName())){
			Log.d(TAG, "Starting DataCollectService");
			this.startService(intent);
		}
		
		if (!isServiceRunning(CommunicationService.class.getName())){			
			if(checkSetting()){
				Log.d(TAG, "Starting CommunicationService");
				this.startService(commIntent);
			}else{
				communicationButton.setChecked(false);
			}
		}
		
		this.copyHtmlToSd();
		
		this.setAddressesOnGUI();
    }
    
    private boolean checkSetting(){
    	Map<String, ?> keys = settings.getAll();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			if (!DataCollectService.serverKeys.contains(entry.getKey()) &&
					settings.getBoolean(entry.getKey(),false)) // There is something we collect.
				return true;
		}
		return false;
    }
   
	private void checkWifiAvaiable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifi != null ) {
			if(!wifi.isAvailable()) // Wifi is disabled, notify user and navigate to settings.
			{
				setupAlertDialog("Wi-Fi �llapot",
						"Wi-Fi kapcsolat nincs enged�lyezve. K�v�nja enged�lyezni? En�lk�l a kommunik�ci�s modul nem tudja feladat�t elv�gezni.",
						SENDER_WIFI);				
			}			
		}
		
	}
	
	@Override
	protected void onStart() {		
		super.onStart();
		if (isServiceRunning(DataCollectService.class.getName())){ // PendingIntent causes binding without a running service.
			this.bindService(intent, mConnection, 0);
		}
		if (isServiceRunning(CommunicationService.class.getName())){
			this.bindService(commIntent, commConnection, 0);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        if (commBound) {
            unbindService(commConnection);
            commBound = false;
        }
	}

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ServiceBinder binder = (ServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    
    private ServiceConnection commConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CommServiceBinder binder = (CommServiceBinder) service;
            commService = binder.getService();
            commBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            commBound = false;
        }
    };


	public void onClick(View v){
		
		//navigate to the details
		intent = new Intent(this, DetailsActivity.class);
		intent.putExtra("id", v.getId());
		this.startActivity(intent);		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			startSettings();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void startSettings(){
		Intent i = new Intent();
		i.setClass(this, ActivityFragmentSettings.class);
		//sending the listener availability in a bundle
		for (String sharedKey : DataCollectService.sharedPrefKeys){
			//if listener does not exist, then put true
			i.putExtra(sharedKey, this.mService.getListener(sharedKey)==null ? true : 
				this.mService.getListener(sharedKey).isAvailable());
		}
		startActivityForResult(i, REQUEST_CODE_OPTIONS);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_CODE_OPTIONS){
			if (!isServiceRunning(CommunicationService.class.getName())){
				Log.d(TAG, "Starting CommunicationService");
				this.startService(commIntent);
				communicationButton.setChecked(true);
			}
			this.setAddressesOnGUI();
		}
	}
	
	private void setAddressesOnGUI(){
		
		EditText et = (EditText)this.findViewById(R.id.nodeAddress);
		et.setKeyListener(null);
		et.setText(Constants.getNodeServerAddress(this));
		
		et = (EditText)this.findViewById(R.id.gcmAddress);
		et.setText(Constants.getGCMServerAddress(this));
		et.setKeyListener(null);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		if (DataCollectService.DEC_NODE_IP.equals(key)){
			Constants.NodeServerIP = sharedPreferences.getString(key, Constants.NodeServerIP);
		}
		else if (DataCollectService.DEC_ADMIN_IP.equals(key)){
			Constants.GCMServerIP = sharedPreferences.getString(key, Constants.GCMServerIP);
		}
		else if (DataCollectService.DEC_NODE_PORT.equals(key)){
			Constants.NodeServerPort = sharedPreferences.getString(key, Constants.NodeServerPort);
		}
		else if (DataCollectService.DEC_ADMIN_PORT.equals(key)){
			Constants.GCMServerPort = sharedPreferences.getString(key, Constants.GCMServerPort);
		}
		
		//finding which shared preference was changed, and register/unregister according to the value
		
		else if (DataCollectService.sharedPrefKeys.contains(key)){
			IListener listener = mService.getListener(key);
			if (listener != null){
				//register/unregister listener
				if (sharedPreferences.getBoolean(key, false)){
					if (listener.isAvailable()){ listener.register(); }
				}					
				else{
					if (listener.isAvailable()){ listener.unregister(); }
				}	
			}
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
	
	public void stopClicked(View v){
		
		switch (v.getId())
		{
		case R.id.buttonStop:
			if (isServiceRunning(DataCollectService.class.getName())){
				stopService(intent);
				if (mBound) {
		            unbindService(mConnection);
		            mBound = false;
		        }				
			}
			else{
				startService(intent);
				this.bindService(intent, mConnection, 0);				
			}
			break;
		case R.id.buttonCommunicationStop:
			if (isServiceRunning(CommunicationService.class.getName())) {
				stopService(commIntent);
				if (commBound) {
					unbindService(commConnection);
					commBound = false;
				}
			} else {
				if (checkSetting()) {
					startService(commIntent);
					this.bindService(commIntent, commConnection, 0);
				} else {
					communicationButton.setChecked(false);
					setupAlertDialog(
							"Kommunik�ci� ind�t�sa",
							"A be�ll�t�sokn�l nem adott meg gy�jtend� adatot, �gy a modul nem ind�that�. K�rem, �ll�tson be gy�jtend� adatot!",
							SENDER_COMMUNICATION);
				}
			}
			break;
		}
	}
	
	private void setupAlertDialog(String title, String msg, int senderId){
		final int sender=senderId;
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setTitle(title);
		alertbox.setMessage(msg);
		alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface arg0, int arg1) {
			switch (sender) {
			case SENDER_COMMUNICATION:
				startSettings();
				break;
			case SENDER_WIFI:
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				break;
			default:
				break;
			}
			}
		});
		alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				 // No
				}
			});
		alertbox.show();
	}
	
	public void communicationDetailsClicked(View view){
		Intent i = new Intent(this, CommunicationActivity.class);
		this.startActivity(i);
	}
	
	//starting CameraActivity to take pictures
	public void uploadImages(View v){				
		//TODO get permission
				
		//if ImageData sharedpref is enabled and DataCollectService is bound, add notification
		if (DataCollectService.isDataTypeEnabled(this, DataCollectService.IMAGE)){
			intent = new Intent(this, CameraActivity.class);
			intent.putExtra("address", Constants.getDataCollectorServerAddress(this));
			this.startActivity(intent);
		}
		
	}
	
	public void sendData(View v){
		//send here some data
		
		Log.d(TAG, "Sending some columns");
		this.queue.add(new DataUploadTask(this, "AccelerationData", "1", Constants.getDataCollectorServerAddress(this), Arrays.asList("id", "timestamp", "accX", "accY")));
		
		Log.d(TAG, "Sending all columns");
		this.queue.add(new DataUploadTask(this, "LightData", "2", Constants.getDataCollectorServerAddress(this)));
		
		Log.d(TAG, "Sending all columns after date");		
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy/MM/dd").parse("2013/09/01");
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage());
		}
		this.queue.add(new DataUploadTask(this, "AccelerationData", "3", Constants.getDataCollectorServerAddress(this), date));
		
		Log.d(TAG, "Sending buggy name");
		this.queue.add(new DataUploadTask(this, "NotExists", "4", Constants.getDataCollectorServerAddress(this)));
		
		Log.d(TAG, "Sending some columns after date");
		this.queue.add(new DataUploadTask(this, "AccelerationData", "5", Constants.getDataCollectorServerAddress(this), date, Arrays.asList("id")));
	}
	
	public void loadJavascript(View v){
		
		//Testing AlgorithmActivity
		intent = new Intent(this, AlgorithmActivity.class);
		this.startActivity(intent);
	}

	@Override
	protected void onDestroy() {		
		super.onDestroy();
	}
	
	/**
	 * Helper method to copy the sensors.html file from /assets to the sd card
	 */
	public void copyHtmlToSd() {

		AssetManager am = this.getAssets();
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			// Create new file to copy into.
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/DistributedAlgos/sensors.html");
			file.createNewFile();
			is = am.open("sensors.html");
			fos = new FileOutputStream(file);

			FileUtils.copy(is, fos);

		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
				}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
